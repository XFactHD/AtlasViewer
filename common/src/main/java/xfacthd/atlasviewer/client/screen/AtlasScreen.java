package xfacthd.atlasviewer.client.screen;

import com.google.common.base.Stopwatch;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL13;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.mixin.*;
import xfacthd.atlasviewer.client.screen.widget.*;
import xfacthd.atlasviewer.client.screen.widget.search.SearchBox;
import xfacthd.atlasviewer.client.screen.widget.search.SearchHandler;
import xfacthd.atlasviewer.client.util.*;
import xfacthd.atlasviewer.platform.Services;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@SuppressWarnings("deprecation")
public final class AtlasScreen extends Screen implements SearchHandler
{
    public static final ResourceLocation BACKGROUND_LOC = new ResourceLocation("minecraft", "textures/gui/demo_background.png");
    public static final ResourceLocation CHECKER_LOC = new ResourceLocation(AtlasViewer.MOD_ID, "textures/gui/checker.png");
    public static final NineSlice BACKGROUND = new NineSlice(0, 0, 248, 166, 256, 256, 4);
    public static final NineSlice CHECKER = new NineSlice(0, 0, 256, 256, 256, 256, 0);
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
    private static final int TOOL_MENU_Y = PADDING * 3;
    private static final Map<TextureAtlas, Size> ATLAS_SIZES = new WeakHashMap<>();

    private int atlasLeft;
    private int atlasTop;
    private int maxAtlasWidth;
    private int maxAtlasHeight;
    private MenuContainer menu;
    private IndicatorButton btnHighlightAnim;
    private Button btnExport;
    private Button btnExportMipped;
    private DiscreteSliderButton mipLevelSlider;
    private SearchBox searchBar;
    private Map<ResourceLocation, TextureAtlas> atlases;
    private TextureAtlas currentAtlas;
    private QuadTree<TextureAtlasSprite> spriteTree;
    private Collection<TextureAtlasSprite> sprites;
    private Size atlasSize;
    private AtlasInfoScreen.AtlasInfo cachedInfo;
    private double atlasScale = 1F;
    private double scrollScale = 1F;
    private float offsetX = 0;
    private float offsetY = 0;
    private final List<Rect2i> animatedLocations = new ArrayList<>();
    private final List<Rect2i> searchResultLocations = new ArrayList<>();
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
        menu.addMenuEntry(searchBar = new SearchBox(
                0, 0, SEARCH_BAR_WIDTH, SEARCH_BAR_HEIGHT, searchBar, this, this::addRenderableWidget
        ));
        menu.arrangeElements();

        atlases = new HashMap<>();
        ((AccessorTextureManager) Minecraft.getInstance().getTextureManager()).atlasviewer$getByPath().forEach((loc, tex) ->
        {
            if (tex instanceof TextureAtlas atlas)
            {
                atlases.put(loc, atlas);
            }
        });

        for (ResourceLocation loc : atlases.keySet())
        {
            atlasSelection.addEntry(new AtlasEntry(loc));
        }

        ResourceLocation currLoc = currentAtlas != null && atlases.containsKey(currentAtlas.location())
                ? currentAtlas.location()
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
        super.renderBackground(graphics, mouseX, mouseY, partialTick);

        RenderSystem.setShaderTexture(0, BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), PADDING, PADDING, 0, width - (PADDING * 2), height - (PADDING * 2), BACKGROUND);

        graphics.drawString(font, title, PADDING * 3, PADDING * 3, 0x404040, false);

        float scale = (float)(atlasScale * scrollScale);

        RenderSystem.setShaderTexture(0, CHECKER_LOC);
        int bgWidth = (int)Math.min(maxAtlasWidth, atlasSize.width * scale);
        int bgHeight = (int)Math.min(maxAtlasHeight, atlasSize.height * scale);
        ClientUtils.drawNineSliceTexture(graphics.pose(), atlasLeft, atlasTop, 0, bgWidth, bgHeight, CHECKER);

        RenderSystem.setShaderTexture(0, currentAtlas.location());

        graphics.enableScissor(atlasLeft, atlasTop, atlasLeft + maxAtlasWidth, atlasTop + maxAtlasHeight);

        RenderSystem.enableBlend();
        setAtlasMipLevel(currentAtlas, currentMipLevel);
        TextureDrawer.drawGuiTexture(
                graphics.pose(),
                atlasLeft + offsetX,
                atlasTop + offsetY,
                0,
                atlasSize.width * scale,
                atlasSize.height * scale,
                0F, 1F, 0F, 1F
        );
        setAtlasMipLevel(currentAtlas, 0);
        RenderSystem.disableBlend();

        graphics.disableScissor();

        graphics.enableScissor(atlasLeft - 1, atlasTop - 1, atlasLeft + maxAtlasWidth + 1, atlasTop + maxAtlasHeight + 1);

        boolean cursorOnAtlas = mouseX >= atlasLeft && mouseX <= (atlasLeft + maxAtlasWidth) && mouseY >= atlasTop && mouseY <= (atlasTop + maxAtlasHeight);
        boolean highlightAnimated = btnHighlightAnim.isChecked();
        boolean hasSearchResults = !searchResultLocations.isEmpty();

        if (highlightAnimated || hasSearchResults || cursorOnAtlas)
        {
            TextureDrawer.startColored();
        }

        if (highlightAnimated && !animatedLocations.isEmpty())
        {
            for (Rect2i rect : animatedLocations)
            {
                drawColoredBox(graphics.pose(), rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), scale, false, 0x00FF00FF);
            }
        }

        if (hasSearchResults)
        {
            for (Rect2i rect : searchResultLocations)
            {
                boolean focused = ((System.currentTimeMillis() / 200L) % 2L == 0L) && focusedSearchResultIdx == searchResultLocations.indexOf(rect);
                drawColoredBox(graphics.pose(), rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), scale, false, focused ? 0xCC00FFFF : 0xFFBB00FF);
            }
        }

        if (cursorOnAtlas)
        {
            int mx = (int)((mouseX - atlasLeft - offsetX) * (1F / atlasScale) / scrollScale);
            int my = (int)((mouseY - atlasTop - offsetY) * (1F / atlasScale) / scrollScale);
            TextureAtlasSprite sprite = spriteTree.find(mx, my);
            hoveredSprite = sprite;
            if (sprite != null)
            {
                SpriteContents contents = sprite.contents();
                drawColoredBox(graphics.pose(), sprite.getX(), sprite.getY(), contents.width(), contents.height(), scale, true, 0xFF0000FF);
            }
        }

        if (highlightAnimated || hasSearchResults || cursorOnAtlas)
        {
            TextureDrawer.end();
        }

        graphics.disableScissor();

        menu.render(graphics.pose());

        if (btnExport.isHovered())
        {
            setTooltipForNextRenderPass(MSG_EXPORT_DETAILS);
        }
        else if (btnExportMipped.active && btnExportMipped.isHovered())
        {
            setTooltipForNextRenderPass(Component.translatable("msg.atlasviewer.export_mipped_atlas.detail", currentMipLevel));
        }
    }

    public static void setAtlasMipLevel(TextureAtlas atlas, int level)
    {
        RenderSystem.activeTexture(GL13.GL_TEXTURE0);
        RenderSystem.bindTexture(atlas.getId());
        RenderSystem.texParameter(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, level);
    }

    private void drawColoredBox(PoseStack poseStack, int x, int y, int width, int height, float scale, boolean expand, int color)
    {
        float sx = x * scale + atlasLeft + offsetX;
        float sy = y * scale + atlasTop + offsetY;
        float sw = width * scale;
        float sh = height * scale;

        if (expand)
        {
            sx--;
            sy--;

            sw += 2;
            sh += 2;
        }

        ClientUtils.drawColoredBox(poseStack, sx, sy, 0, sw, sh, color);
    }

    @Override
    public void tick()
    {
        searchBar.tick();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY)
    {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY))
        {
            return true;
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && mouseX >= atlasLeft && mouseX <= (atlasLeft + maxAtlasWidth) && mouseY >= atlasTop && mouseY <= (atlasTop + maxAtlasHeight))
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
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (menu.isOpen() && !menu.isMouseOver(mouseX, mouseY))
        {
            menu.setOpen(false);
        }
        if (!super.mouseClicked(mouseX, mouseY, button))
        {
            setFocused(null);
            return false;
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (super.mouseReleased(mouseX, mouseY, button))
        {
            return true;
        }

        if (hoveredSprite != null && button == GLFW.GLFW_MOUSE_BUTTON_2 && (!menu.isOpen() || !menu.isMouseOver(mouseX, mouseY)))
        {
            Services.PLATFORM.pushScreenLayer(new SpriteInfoScreen(currentAtlas, hoveredSprite, currentMipLevel));
            return true;
        }

        return false;
    }

    private void selectAtlas(AtlasEntry entry)
    {
        currentAtlas = atlases.get(entry.atlas);

        atlasSize = ATLAS_SIZES.get(currentAtlas);
        atlasScale = (float) maxAtlasWidth / atlasSize.width;
        if (atlasSize.height * atlasScale > maxAtlasHeight)
        {
            atlasScale = (float) maxAtlasHeight / atlasSize.height;
        }

        sprites = ((AccessorTextureAtlas) currentAtlas).atlasviewer$getTexturesByName().values();

        Rect2i treeRect = new Rect2i(0, 0, atlasSize.width, atlasSize.height);
        int minSize = sprites.stream()
                .map(TextureAtlasSprite::contents)
                .mapToInt(c -> Math.max(c.width(), c.height()))
                .min()
                .orElseThrow();
        spriteTree = new QuadTree<>(treeRect, minSize);
        sprites.forEach(s -> spriteTree.insert(s, AtlasScreen::getSpriteSize));
        Rect2i minRect = spriteTree.minSize();
        AtlasViewer.LOGGER.debug(
                "QuadTree for atlas '{}' has a depth of {}. Smallest sub-tree sized {}x{}, requested {}x{}",
                currentAtlas.location(),
                spriteTree.depth(),
                minRect.getWidth(), minRect.getHeight(),
                minSize, minSize
        );

        int mipLevels = ((AccessorTextureAtlas) currentAtlas).atlasviewer$getMipLevel();
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
        sprites.stream()
                .filter(sprite -> ((AccessorSpriteContents) sprite.contents()).atlasviewer$getAnimatedTexture() != null)
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
        Size size = ATLAS_SIZES.get(currentAtlas);
        int width = size.width >> mipLevel;
        int height = size.height >> mipLevel;
        try (NativeImage image = new NativeImage(width, height, false))
        {
            int texId = currentAtlas.getId();
            RenderSystem.bindTexture(texId);
            image.downloadTexture(mipLevel, false);
            exportNativeImage(image, currentAtlas.location(), "atlas", true, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting selected texture atlas", e);
            Services.PLATFORM.pushScreenLayer(MessageScreen.error(List.of(
                    MSG_EXPORT_ERROR,
                    Component.literal(e.toString()).withStyle(ChatFormatting.DARK_RED)
            )));
        }
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
            cachedInfo = AtlasInfoScreen.computeInfo(currentAtlas, sprites);
            stopwatch.stop();
            AtlasViewer.LOGGER.debug("Took {} to compute atlas info for atlas '{}'", stopwatch, currentAtlas.location());
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
            sprites.forEach(sprite ->
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
     */
    public static void exportNativeImage(NativeImage image, ResourceLocation name, String prefix, boolean shortenPath, Component msgSuccess) throws IOException
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
        if (!fileName.endsWith(".png")) //Texture atlas name already ends with .png
        {
            fileName += ".png";
        }

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
    }

    private static Component buildPathComponent(Path path)
    {
        path = path.getParent().toAbsolutePath().normalize();
        return Component.literal(path.toString())
                .setStyle(Style.EMPTY
                        .withColor(ChatFormatting.DARK_GRAY)
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, HOVER_MSG_CLICK_TO_OPEN))
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path.toString()))
                );
    }

    private record Size(int width, int height) { }

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
