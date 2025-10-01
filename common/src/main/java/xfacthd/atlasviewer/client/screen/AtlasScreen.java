package xfacthd.atlasviewer.client.screen;

import com.google.common.base.Stopwatch;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.screen.widget.BackgroundSwitchButton;
import xfacthd.atlasviewer.client.screen.widget.DiscreteSliderButton;
import xfacthd.atlasviewer.client.screen.widget.IndicatorButton;
import xfacthd.atlasviewer.client.screen.widget.MenuContainer;
import xfacthd.atlasviewer.client.screen.widget.SelectionWidget;
import xfacthd.atlasviewer.client.screen.widget.search.SearchBox;
import xfacthd.atlasviewer.client.screen.widget.search.SearchHandler;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.QuadTree;
import xfacthd.atlasviewer.client.util.Rect2i;
import xfacthd.atlasviewer.platform.Services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

@SuppressWarnings("deprecation")
public final class AtlasScreen extends AtlasViewerScreen implements SearchHandler
{
    public static final ResourceLocation BACKGROUND_LOC = AtlasViewer.rl("background");
    private static final Component TITLE = Component.translatable("title.atlasviewer.atlasviewer");
    private static final Component TITLE_HIGHLIGHT_ANIM = Component.translatable("btn.atlasviewer.highlight_animated");
    private static final Component TITLE_EXPORT = Component.translatable("btn.atlasviewer.export_atlas");
    private static final Component TITLE_EXPORT_MIPPED = Component.translatable("btn.atlasviewer.export_mipped_atlas");
    private static final Component TITLE_TOOLS = Component.translatable("btn.atlasviewer.menu");
    private static final Component TITLE_DETAILS = Component.translatable("btn.atlasviewer.details");
    private static final Component MSG_EXPORT_DETAILS = Component.translatable("msg.atlasviewer.export_atlas.detail");
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_atlas_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_atlas_error");
    private static final Component HOVER_MSG_CLICK_TO_OPEN = Component.translatable("hover.atlasviewer.path.click");
    private static final int PADDING = 5;
    private static final int HIGHLIGHT_ANIM_WIDTH = 160;
    private static final int HIGHLIGHT_ANIM_HEIGHT = 20;
    private static final int EXPORT_WIDTH = 100;
    private static final int EXPORT_HEIGHT = 20;
    private static final int SEARCH_BAR_WIDTH = 198;
    private static final int SEARCH_BAR_HEIGHT = 20;
    private static final int SELECT_WIDTH = 300;
    private static final int SELECT_HEIGHT = 20;
    private static final int DETAILS_WIDTH = 100;
    private static final int DETAILS_HEIGHT = 20;
    private static final int MIP_LEVEL_WIDTH = 160;
    private static final int MIP_LEVEL_HEIGHT = 20;
    private static final int BG_SWITCHER_WIDTH = 180;
    private static final int TOOL_MENU_Y = PADDING * 3;
    private static final Map<TextureAtlas, Size> ATLAS_SIZES = new WeakHashMap<>();

    private int atlasLeft;
    private int atlasTop;
    private int maxAtlasWidth;
    private int maxAtlasHeight;
    @UnknownNullability
    private MenuContainer menu;
    @UnknownNullability
    private IndicatorButton btnHighlightAnim;
    @UnknownNullability
    private Button btnExport;
    @UnknownNullability
    private Button btnExportMipped;
    @UnknownNullability
    private DiscreteSliderButton mipLevelSlider;
    @UnknownNullability
    private BackgroundSwitchButton bgSwitchButton;
    @UnknownNullability
    private SearchBox searchBar;
    private final Map<ResourceLocation, AtlasManager.AtlasEntry> atlases = new HashMap<>();
    @Nullable
    private AtlasManager.AtlasEntry currentAtlas;
    @Nullable
    private QuadTree<TextureAtlasSprite> spriteTree;
    @Nullable
    private Collection<TextureAtlasSprite> sprites;
    private Size atlasSize = new Size(0, 0);
    @Nullable
    private AtlasInfoScreen.AtlasInfo cachedInfo;
    private double atlasScale = 1F;
    private double scrollScale = 1F;
    private float offsetX = 0;
    private float offsetY = 0;
    private final List<Rect2i> animatedLocations = new ArrayList<>();
    private final List<Rect2i> searchResultLocations = new ArrayList<>();
    @Nullable
    private TextureAtlasSprite hoveredSprite = null;
    private int currentMipLevel = 0;
    private int focusedSearchResultIdx = -1;

    public AtlasScreen()
    {
        super(TITLE);
    }

    @Override
    protected void init()
    {
        atlasTop = (PADDING * 4) + SELECT_HEIGHT;
        atlasLeft = PADDING * 3;
        maxAtlasWidth = width - (PADDING * 6);
        maxAtlasHeight = height - atlasTop - (PADDING * 3);

        int titleLen = font.width(TITLE);
        int selectWidth = Math.min(SELECT_WIDTH, width - (PADDING * 8) - titleLen - 40);

        SelectionWidget<AtlasEntry> atlasSelection = new SelectionWidget<>(this, width - (PADDING * 4) - selectWidth - 40, (PADDING * 3), selectWidth, Component.empty(), this::selectAtlas);
        addRenderableWidget(atlasSelection);

        Button menuButton = addRenderableWidget(Button.builder(TITLE_TOOLS, this::toggleMenu)
                .pos(width - (PADDING * 3) - 40, TOOL_MENU_Y)
                .size(40, 20)
                .build()
        );
        menu = new MenuContainer(menuButton, true);
        menu.addMenuEntry(btnHighlightAnim = addRenderableWidget(new IndicatorButton(
                0, 0,
                HIGHLIGHT_ANIM_WIDTH, HIGHLIGHT_ANIM_HEIGHT,
                TITLE_HIGHLIGHT_ANIM,
                btnHighlightAnim,
                this::highlightAnimated
        )));
        menu.addMenuEntry(btnExport = addRenderableWidget(
                Button.builder(TITLE_EXPORT, this::exportAtlas)
                        .pos(0, 0)
                        .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                        .build()
        ));
        menu.addMenuEntry(btnExportMipped = addRenderableWidget(
                Button.builder(TITLE_EXPORT_MIPPED, this::exportAtlasMipped)
                        .pos(0, 0)
                        .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                        .build()
        ));
        menu.addMenuEntry(addRenderableWidget(
                Button.builder(TITLE_DETAILS, this::openAtlasDetails)
                        .pos(0, 0)
                        .size(DETAILS_WIDTH, DETAILS_HEIGHT)
                        .build()
        ));
        menu.addMenuEntry(mipLevelSlider = addRenderableWidget(
                new DiscreteSliderButton(
                        0, 0,
                        MIP_LEVEL_WIDTH, MIP_LEVEL_HEIGHT,
                        "btn.atlasviewer.mip_level",
                        mipLevelSlider != null ? mipLevelSlider.getStep() : 0,
                        mipLevelSlider != null ? mipLevelSlider.getMaxStep() : 0,
                        this::selectMipLevel
                )
        ));
        menu.addMenuEntry(bgSwitchButton = addRenderableWidget(new BackgroundSwitchButton(
                0, 0,
                BG_SWITCHER_WIDTH,
                bgSwitchButton != null ? bgSwitchButton.getSelectedType() : null
        )));
        menu.addMenuEntry(searchBar = new SearchBox(
                0, 0, SEARCH_BAR_WIDTH, SEARCH_BAR_HEIGHT, searchBar, this, this::addRenderableWidget
        ));
        menu.arrangeElements();

        atlases.clear();
        atlases.putAll(minecraft().getAtlasManager().atlasviewer$getAtlasesByTexture());

        for (ResourceLocation loc : atlases.keySet())
        {
            atlasSelection.addEntry(new AtlasEntry(loc));
        }

        ResourceLocation currLoc = currentAtlas != null && atlases.containsKey(currentAtlas.config().textureId())
                ? currentAtlas.config().textureId()
                : TextureAtlas.LOCATION_BLOCKS;
        AtlasEntry current = atlasSelection.stream()
                .filter(entry -> entry.atlas.equals(currLoc))
                .findFirst()
                .orElseThrow();
        atlasSelection.setSelected(current, true);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBlurredBackground(graphics);

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND_LOC, PADDING, PADDING, width - (PADDING * 2), height - (PADDING * 2));

        graphics.drawString(font, title, PADDING * 3, PADDING * 3, 0xFF404040, false);

        float scale = (float)(atlasScale * scrollScale);

        int bgWidth = (int)Math.min(maxAtlasWidth, atlasSize.width * scale);
        int bgHeight = (int)Math.min(maxAtlasHeight, atlasSize.height * scale);
        ResourceLocation bgSprite = bgSwitchButton.getSelectedType().getSprite();
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bgSprite, atlasLeft, atlasTop, bgWidth, bgHeight);

        graphics.enableScissor(atlasLeft, atlasTop, atlasLeft + maxAtlasWidth, atlasTop + maxAtlasHeight);
        Objects.requireNonNull(currentAtlas);
        GpuTextureView atlasTexView = currentAtlas.atlas().atlasview$getMippedTextureView(currentMipLevel);
        ClientUtils.blitSpecial(
                graphics,
                RenderPipelines.GUI_TEXTURED,
                TextureSetup.singleTexture(atlasTexView),
                atlasLeft + offsetX,
                atlasTop + offsetY,
                atlasLeft + offsetX + atlasSize.width * scale,
                atlasTop + offsetY + atlasSize.height * scale,
                0F, 1F, 0F, 1F,
                0xFFFFFFFF
        );
        graphics.disableScissor();

        graphics.enableScissor(atlasLeft - 1, atlasTop - 1, atlasLeft + maxAtlasWidth + 1, atlasTop + maxAtlasHeight + 1);

        boolean cursorOnAtlas = isMouseOverAtlas(mouseX, mouseY);
        boolean highlightAnimated = btnHighlightAnim.isChecked();
        boolean hasSearchResults = !searchResultLocations.isEmpty();

        if (highlightAnimated && !animatedLocations.isEmpty())
        {
            for (Rect2i rect : animatedLocations)
            {
                drawColoredBox(graphics, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), scale, false, 0xFF00FF00);
            }
        }

        if (hasSearchResults)
        {
            for (Rect2i rect : searchResultLocations)
            {
                boolean focused = ((System.currentTimeMillis() / 200L) % 2L == 0L) && focusedSearchResultIdx == searchResultLocations.indexOf(rect);
                drawColoredBox(graphics, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), scale, false, focused ? 0xFFCC00FF : 0xFFFFBB00);
            }
        }

        if (cursorOnAtlas)
        {
            int mx = (int)((mouseX - atlasLeft - offsetX) * (1F / atlasScale) / scrollScale);
            int my = (int)((mouseY - atlasTop - offsetY) * (1F / atlasScale) / scrollScale);
            TextureAtlasSprite sprite = Objects.requireNonNull(spriteTree).find(mx, my);
            hoveredSprite = sprite;
            if (sprite != null)
            {
                SpriteContents contents = sprite.contents();
                drawColoredBox(graphics, sprite.getX(), sprite.getY(), contents.width(), contents.height(), scale, true, 0xFFFF0000);
            }
        }
        else
        {
            hoveredSprite = null;
        }

        graphics.disableScissor();

        menu.render(graphics);

        if (btnExport.isHovered())
        {
            setTooltipForNextFrame(graphics, MSG_EXPORT_DETAILS, mouseX, mouseY);
        }
        else if (btnExportMipped.active && btnExportMipped.isHovered())
        {
            setTooltipForNextFrame(graphics, Component.translatable("msg.atlasviewer.export_mipped_atlas.detail", currentMipLevel), mouseX, mouseY);
        }
    }

    private boolean isMouseOverAtlas(int mouseX, int mouseY)
    {
        if (mouseX < atlasLeft || mouseX > (atlasLeft + maxAtlasWidth)) return false;
        if (mouseY < atlasTop || mouseY > (atlasTop + maxAtlasHeight)) return false;
        return !menu.isOpen() || !menu.isMouseOver(mouseX, mouseY);
    }

    private void drawColoredBox(GuiGraphics graphics, int x, int y, int width, int height, float scale, boolean expand, int color)
    {
        float sx = x * scale + atlasLeft + offsetX;
        float sy = y * scale + atlasTop + offsetY;
        float sw = width * scale;
        float sh = height * scale;

        float nsx = Math.max(sx, atlasLeft);
        float nsy = Math.max(sy, atlasTop);
        sw = Math.min(sw - (nsx - sx), Math.max(atlasLeft + maxAtlasWidth - nsx, 0));
        sh = Math.min(sh - (nsy - sy), Math.max(atlasTop + maxAtlasHeight - nsy, 0));
        sx = nsx;
        sy = nsy;

        if (expand)
        {
            sx--;
            sy--;

            sw += 2;
            sh += 2;
        }

        ClientUtils.drawColoredBox(graphics, sx, sy, sw, sh, color);
    }

    @Override
    public void tick()
    {
        searchBar.tick();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY)
    {
        if (super.mouseDragged(event, dragX, dragY))
        {
            return true;
        }

        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_1 && event.x() >= atlasLeft && event.x() <= (atlasLeft + maxAtlasWidth) && event.y() >= atlasTop && event.y() <= (atlasTop + maxAtlasHeight))
        {
            Window window = Minecraft.getInstance().getWindow();
            float scaleX = window.getGuiScaledWidth() / (float)window.getScreenWidth();
            float scaleY = window.getGuiScaledHeight() / (float)window.getScreenHeight();
            clampOffsetX(offsetX + (float)(dragX * scaleX * window.getGuiScale()));
            clampOffsetY(offsetY + (float)(dragY * scaleY * window.getGuiScale()));
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY)
    {
        if (super.mouseScrolled(mouseX, mouseY, deltaX, deltaY))
        {
            return true;
        }

        if (mouseX >= atlasLeft && mouseX <= (atlasLeft + maxAtlasWidth) && mouseY >= atlasTop && mouseY <= (atlasTop + maxAtlasHeight))
        {
            double prevScale = scrollScale;
            scrollScale = Math.max(scrollScale + (float)(deltaY * .1), 1F);

            double mOffX = mouseX - atlasLeft;
            double mOffY = mouseY - atlasTop;
            double offsetX = (this.offsetX - mOffX) / prevScale * scrollScale + mOffX;
            double offsetY = (this.offsetY - mOffY) / prevScale * scrollScale + mOffY;

            clampOffsetX((float) offsetX);
            clampOffsetY((float) offsetY);

            return true;
        }

        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick)
    {
        if (menu.isOpen() && !menu.isMouseOver(event.x(), event.y()))
        {
            menu.setOpen(false);
        }
        if (!super.mouseClicked(event, doubleClick))
        {
            setFocused(null);
            return false;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event)
    {
        if (super.mouseReleased(event))
        {
            return true;
        }

        if (hoveredSprite != null && event.button() == GLFW.GLFW_MOUSE_BUTTON_2 && (!menu.isOpen() || !menu.isMouseOver(event.x(), event.y())))
        {
            Services.PLATFORM.pushScreenLayer(new SpriteInfoScreen(Objects.requireNonNull(currentAtlas), hoveredSprite, currentMipLevel, bgSwitchButton.getSelectedType()));
            return true;
        }

        return false;
    }

    private void selectAtlas(AtlasEntry entry)
    {
        currentAtlas = atlases.get(entry.atlas);

        atlasSize = ATLAS_SIZES.get(currentAtlas.atlas());
        atlasScale = (float) maxAtlasWidth / atlasSize.width;
        if (atlasSize.height * atlasScale > maxAtlasHeight)
        {
            atlasScale = (float) maxAtlasHeight / atlasSize.height;
        }

        sprites = Objects.requireNonNull(currentAtlas).atlas().atlasviewer$getTexturesByName().values();

        int minSize = sprites.stream()
                .map(TextureAtlasSprite::contents)
                .mapToInt(c -> Math.max(c.width(), c.height()))
                .min()
                .orElseThrow();
        spriteTree = new QuadTree<>(atlasSize.width, atlasSize.height, minSize);
        sprites.forEach(s -> spriteTree.insert(s, AtlasScreen::getSpriteSize));
        spriteTree.trim();
        Rect2i minRect = spriteTree.minSize();
        AtlasViewer.LOGGER.debug(
                "QuadTree for atlas '{}' has a depth of {}. Smallest sub-tree sized {}x{}, requested {}x{}",
                currentAtlas.config().textureId(),
                spriteTree.depth(),
                minRect.getWidth(), minRect.getHeight(),
                minSize, minSize
        );

        int mipLevels = currentAtlas.atlas().atlasviewer$getMipLevel();
        mipLevelSlider.setStep(0, true);
        mipLevelSlider.setMaxStep(mipLevels);
        mipLevelSlider.active = mipLevels > 0;
        btnExportMipped.active = false;

        scrollScale = 1F;
        offsetX = 0;
        offsetY = 0;
        searchBar.clear();
        searchResultLocations.clear();
        focusedSearchResultIdx = -1;
        cachedInfo = null;

        if (btnHighlightAnim.isChecked())
        {
            gatherAnimatedLocations();
        }
    }

    private void highlightAnimated(Button btn)
    {
        if (btnHighlightAnim.isChecked())
        {
            gatherAnimatedLocations();
        }
    }

    private void gatherAnimatedLocations()
    {
        animatedLocations.clear();
        Objects.requireNonNull(sprites).stream()
                .filter(sprite -> sprite.contents().atlasviewer$getAnimatedTexture() != null)
                .forEach(sprite -> animatedLocations.add(getSpriteSize(sprite)));
    }

    private void exportAtlas(Button btn)
    {
        exportAtlas(0);
    }

    private void exportAtlasMipped(Button btn)
    {
        exportAtlas(currentMipLevel);
    }

    private void exportAtlas(int mipLevel)
    {
        ClientUtils.downloadTexture(Objects.requireNonNull(currentAtlas).atlas().getTexture(), mipLevel, image ->
        {
            try
            {
                Path imgPath = exportNativeImage(image, currentAtlas.config().textureId(), "atlas", mipLevel, true, MSG_EXPORT_SUCCESS);
                if (mipLevel == 0)
                {
                    Map<ResourceLocation, TextureAtlasSprite> sprites = currentAtlas.atlas().atlasviewer$getTexturesByName();
                    TextureAtlas.dumpSpriteNames(imgPath.getParent(), imgPath.getFileName().toString(), sprites);
                }
            }
            catch (IOException e)
            {
                AtlasViewer.LOGGER.error("Encountered an error while exporting selected texture atlas", e);
                Services.PLATFORM.pushScreenLayer(MessageScreen.error(List.of(
                        MSG_EXPORT_ERROR,
                        Component.literal(e.toString()).withStyle(ChatFormatting.DARK_RED)
                )));
            }
        });
    }

    private void clampOffsetX(float offsetX) { this.offsetX = clampOffset(atlasSize.width, maxAtlasWidth, offsetX); }

    private void clampOffsetY(float offsetY) { this.offsetY = clampOffset(atlasSize.height, maxAtlasHeight, offsetY); }

    private float clampOffset(float atlasDim, float viewDim, float offset)
    {
        float minOffset = (atlasDim * (float)(atlasScale * scrollScale)) - viewDim;
        minOffset = Math.max(minOffset, 0);
        return Mth.clamp(offset, -minOffset, 0);
    }

    private void toggleMenu(Button btn)
    {
        menu.toggleOpen();
    }

    private void openAtlasDetails(Button btn)
    {
        if (cachedInfo == null)
        {
            Stopwatch stopwatch = Stopwatch.createStarted();
            cachedInfo = AtlasInfoScreen.computeInfo(Objects.requireNonNull(currentAtlas), Objects.requireNonNull(sprites));
            stopwatch.stop();
            AtlasViewer.LOGGER.debug("Took {} to compute atlas info for atlas '{}'", stopwatch, currentAtlas.config().textureId());
        }
        Services.PLATFORM.pushScreenLayer(new AtlasInfoScreen(cachedInfo));
    }

    private void selectMipLevel(int level)
    {
        currentMipLevel = level;
        btnExportMipped.active = level > 0;
    }

    @Override
    public int getResultCount()
    {
        return searchResultLocations.size();
    }

    @Override
    public void updateSearch(String text)
    {
        searchResultLocations.clear();
        focusedSearchResultIdx = -1;

        if (!text.isEmpty())
        {
            Objects.requireNonNull(sprites).forEach(sprite ->
            {
                if (sprite.contents().name().toString().contains(text))
                {
                    searchResultLocations.add(getSpriteSize(sprite));
                }
            });
            searchResultLocations.sort(Comparator.comparingInt(Rect2i::getY).thenComparing(Rect2i::getX));
        }
    }

    @Override
    public void jumpToNextResult()
    {
        if (!searchResultLocations.isEmpty())
        {
            focusedSearchResultIdx = (focusedSearchResultIdx + 1) % searchResultLocations.size();
            Rect2i result = searchResultLocations.get(focusedSearchResultIdx);
            scrollScale = Math.max(1F / atlasScale, 1F);

            double cx = (result.getX() + (result.getWidth() / 2F));
            double cy = (result.getY() + (result.getHeight() / 2F));
            double scale = atlasScale * scrollScale;
            clampOffsetX((float) -(((cx - (maxAtlasWidth / scale)) * scale) + (maxAtlasWidth / 2F)));
            clampOffsetY((float) -(((cy - (maxAtlasHeight / scale)) * scale) + (maxAtlasHeight / 2F)));
        }
    }

    @Override
    public int getFocusedResultIndex()
    {
        return focusedSearchResultIdx;
    }



    private static Rect2i getSpriteSize(TextureAtlasSprite sprite)
    {
        SpriteContents contents = sprite.contents();
        return new Rect2i(sprite.getX(), sprite.getY(), contents.width(), contents.height());
    }

    public static void storeAtlasSize(TextureAtlas atlas, int width, int height)
    {
        ATLAS_SIZES.put(atlas, new Size(width, height));
    }

    /**
     * Exports a {@link NativeImage} to a file according to the given {@link ResourceLocation}
     * @param image The image to export
     * @param name The original name of the resource to export
     * @param prefix The type prefix of the image (i.e. "atlas" for a texture atlas or "sprite" for a single sprite)
     * @param shortenPath If true, only the part of the name after the last slash will be used as part of the file name
     * @return The file path of the exported atlas image
     */
    public static Path exportNativeImage(NativeImage image, ResourceLocation name, String prefix, int mipLevel, boolean shortenPath, Component msgSuccess) throws IOException
    {
        Path folderPath = Services.PLATFORM.getGameDir().resolve("atlasviewer");
        Files.createDirectories(folderPath);

        String texPath = name.getPath();
        if (shortenPath)
        {
            int idx = texPath.lastIndexOf('/');
            texPath = texPath.substring(idx == -1 ? 0 : (idx + 1));
        }
        else
        {
            texPath = texPath.replace('/', '-');
        }
        String fileName = prefix + "_" + name.getNamespace() + "_" + texPath;
        if (fileName.endsWith(".png")) //Texture atlas name already ends with .png
        {
            fileName = fileName.substring(0, fileName.length() - 4);
        }
        if (mipLevel > 0)
        {
            fileName += "_" + mipLevel;
        }
        fileName += ".png";

        Path filePath = folderPath.resolve(fileName);
        if (Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS))
        {
            Files.createFile(filePath);
        }
        image.writeToFile(filePath);

        Services.PLATFORM.pushScreenLayer(MessageScreen.info(List.of(
                msgSuccess,
                buildPathComponent(filePath)
        )));

        return filePath;
    }

    private static Component buildPathComponent(Path path)
    {
        path = path.getParent().toAbsolutePath().normalize();
        return Component.literal(path.toString())
                .setStyle(Style.EMPTY
                        .withColor(ChatFormatting.DARK_GRAY)
                        .withHoverEvent(new HoverEvent.ShowText(HOVER_MSG_CLICK_TO_OPEN))
                        .withClickEvent(new ClickEvent.OpenFile(path.toString()))
                );
    }

    public record Size(int width, int height) { }

    private static class AtlasEntry extends SelectionWidget.SelectionEntry<AtlasEntry>
    {
        private final ResourceLocation atlas;

        public AtlasEntry(ResourceLocation atlas)
        {
            super(Component.literal(atlas.toString()));
            this.atlas = atlas;
        }
    }
}
