package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.mixin.*;
import xfacthd.atlasviewer.client.screen.widget.SelectionWidget;
import xfacthd.atlasviewer.client.util.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@SuppressWarnings("deprecation")
public class AtlasScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.atlasviewer");
    private static final Component TITLE_EXPORT = Component.translatable("btn.atlasviewer.export_atlas");
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_atlas_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_atlas_error");
    private static final Component HOVER_MSG_CLICK_TO_OPEN = Component.translatable("hover.atlasviewer.path.click");
    private static final int PADDING = 5;
    private static final int EXPORT_WIDTH = 100;
    private static final int EXPORT_HEIGHT = 20;
    private static final int SELECT_WIDTH = 300;
    private static final int SELECT_HEIGHT = 20;
    private static final Map<TextureAtlas, Size> ATLAS_SIZES = new WeakHashMap<>();

    private int atlasLeft;
    private int atlasTop;
    private int maxAtlasWidth;
    private int maxAtlasHeight;
    private Map<ResourceLocation, TextureAtlas> atlases;
    private TextureAtlas currentAtlas;
    private QuadTree<TextureAtlasSprite> spriteTree;
    private Size atlasSize;
    private double atlasScale = 1F;
    private double scrollScale = 1F;
    private float offsetX = 0;
    private float offsetY = 0;
    private TextureAtlasSprite hoveredSprite = null;

    public AtlasScreen() { super(TITLE); }

    @Override
    protected void init()
    {
        atlasTop = (PADDING * 4) + SELECT_HEIGHT;
        atlasLeft = PADDING * 3;
        maxAtlasWidth = width - (PADDING * 6);
        maxAtlasHeight = height - atlasTop - (PADDING * 3);

        addRenderableWidget(Button.builder(TITLE_EXPORT, this::exportAtlas)
                .pos(width - (PADDING * 4) - SELECT_WIDTH - EXPORT_WIDTH, PADDING * 3)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );

        SelectionWidget<AtlasEntry> atlasSelection = new SelectionWidget<>(width - (PADDING * 3) - SELECT_WIDTH, (PADDING * 3), SELECT_WIDTH, Component.empty(), this::selectAtlas);
        addRenderableWidget(atlasSelection);

        atlases = new HashMap<>();
        ((AccessorTextureManager) Minecraft.getInstance().textureManager).getByPath().forEach((loc, tex) ->
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
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/demo_background.png"));
        ClientUtils.drawNineSliceTexture(this, poseStack, PADDING, PADDING, width - (PADDING * 2), height - (PADDING * 2), 248, 166, 4);

        font.draw(poseStack, title, PADDING * 3, PADDING * 3, 0x404040);

        float scale = (float)(atlasScale * scrollScale);

        RenderSystem.setShaderTexture(0, new ResourceLocation(AtlasViewer.MOD_ID, "textures/gui/checker.png"));
        int bgWidth = (int)Math.min(maxAtlasWidth, atlasSize.width * scale);
        int bgHeight = (int)Math.min(maxAtlasHeight, atlasSize.height * scale);
        ClientUtils.drawNineSliceTexture(this, poseStack, atlasLeft, atlasTop, bgWidth, bgHeight, 256, 256, 0);

        RenderSystem.setShaderTexture(0, currentAtlas.location());

        Window window = Minecraft.getInstance().getWindow();
        int windowHeight = window.getGuiScaledHeight();
        double windowScale = window.getGuiScale();
        RenderSystem.enableScissor(
                (int)(atlasLeft * windowScale),
                (int)((windowHeight - (atlasTop + maxAtlasHeight)) * windowScale - 1),
                (int)(maxAtlasWidth * windowScale),
                (int)((maxAtlasHeight) * windowScale)
        );

        RenderSystem.enableBlend();
        TextureDrawer.drawGuiTexture(
                poseStack,
                this,
                atlasLeft + offsetX,
                atlasTop + offsetY,
                atlasSize.width * scale,
                atlasSize.height * scale,
                0F, 1F, 0F, 1F
        );
        RenderSystem.disableBlend();

        RenderSystem.disableScissor();

        RenderSystem.enableScissor(
                (int)((atlasLeft - 1) * windowScale),
                (int)((windowHeight - (atlasTop + maxAtlasHeight) - 1) * windowScale - 1),
                (int)((maxAtlasWidth + 2) * windowScale),
                (int)((maxAtlasHeight + 2) * windowScale)
        );

        if (mouseX >= atlasLeft && mouseX <= (atlasLeft + maxAtlasWidth) && mouseY >= atlasTop && mouseY <= (atlasTop + maxAtlasHeight))
        {
            int mx = (int)((mouseX - atlasLeft - offsetX) * (1F / atlasScale) / scrollScale);
            int my = (int)((mouseY - atlasTop - offsetY) * (1F / atlasScale) / scrollScale);
            TextureAtlasSprite sprite = spriteTree.find(mx, my);
            hoveredSprite = sprite;
            if (sprite != null)
            {
                SpriteContents contents = sprite.contents();
                float sx = sprite.getX() * scale + atlasLeft + offsetX;
                float sy = sprite.getY() * scale + atlasTop + offsetY;
                float sw = contents.width() * scale;
                float sh = contents.height() * scale;
                TextureDrawer.startColored();
                TextureDrawer.fillGuiColorBuffer(poseStack, this, sx - 1F, sy - 1F, 1F, sh + 2F, 0xFF0000FF);
                TextureDrawer.fillGuiColorBuffer(poseStack, this, sx + sw, sy - 1F, 1F, sh + 2F, 0xFF0000FF);
                TextureDrawer.fillGuiColorBuffer(poseStack, this, sx,      sy - 1F, sw, 1F,      0xFF0000FF);
                TextureDrawer.fillGuiColorBuffer(poseStack, this, sx,      sy + sh, sw, 1F,      0xFF0000FF);
                TextureDrawer.end();
            }
        }

        RenderSystem.disableScissor();

        super.render(poseStack, mouseX, mouseY, partialTick);
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
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        if (super.mouseScrolled(mouseX, mouseY, delta))
        {
            return true;
        }

        if (mouseX >= atlasLeft && mouseX <= (atlasLeft + maxAtlasWidth) && mouseY >= atlasTop && mouseY <= (atlasTop + maxAtlasHeight))
        {
            double prevScale = scrollScale;
            scrollScale = Math.max(scrollScale + (float)(delta * .1), 1F);

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
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if (super.mouseReleased(mouseX, mouseY, button))
        {
            return true;
        }

        if (hoveredSprite != null && button == GLFW.GLFW_MOUSE_BUTTON_2)
        {
            Minecraft.getInstance().pushGuiLayer(new SpriteInfoScreen(hoveredSprite));
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

        Collection<TextureAtlasSprite> sprites = ((AccessorTextureAtlas) currentAtlas).getTexturesByName().values();

        Rect2i treeRect = new Rect2i(0, 0, atlasSize.width, atlasSize.height);
        int minSize = sprites.stream()
                .map(TextureAtlasSprite::contents)
                .mapToInt(c -> Math.min(c.width(), c.height()))
                .min()
                .orElseThrow();
        spriteTree = new QuadTree<>(treeRect, minSize);
        sprites.forEach(s -> spriteTree.insert(s, AtlasScreen::getSpriteSize));

        scrollScale = 1F;
        offsetX = 0;
        offsetY = 0;
    }

    private void exportAtlas(Button btn)
    {
        Size size = ATLAS_SIZES.get(currentAtlas);
        try (NativeImage image = new NativeImage(size.width(), size.height(), false))
        {
            int texId = currentAtlas.getId();
            RenderSystem.bindTexture(texId);
            image.downloadTexture(0, false);
            exportNativeImage(image, currentAtlas.location(), "atlas", true, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting selected texture atlas", e);
            Minecraft.getInstance().pushGuiLayer(MessageScreen.error(List.of(
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
        Path folderPath = FMLPaths.GAMEDIR.get().resolve("atlasviewer");
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

        Minecraft.getInstance().pushGuiLayer(MessageScreen.info(List.of(
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

    private static class AtlasEntry extends SelectionWidget.SelectionEntry
    {
        private final ResourceLocation atlas;

        public AtlasEntry(ResourceLocation atlas)
        {
            super(Component.literal(atlas.toString()));
            this.atlas = atlas;
        }
    }
}
