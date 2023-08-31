package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.resource.DelegatingPackResources;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.mixin.*;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.screen.widget.DiscreteSliderButton;
import xfacthd.atlasviewer.client.util.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpriteInfoScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.spriteinfo");
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.spritename");
    private static final Component LABEL_WIDTH = Component.translatable("label.atlasviewer.spritewidth");
    private static final Component LABEL_HEIGHT = Component.translatable("label.atlasviewer.spriteheight");
    private static final Component LABEL_SOURCE = Component.translatable("label.atlasviewer.spritesource");
    private static final Component LABEL_ANIMATED = Component.translatable("label.atlasviewer.spriteanimated");
    private static final Component LABEL_FRAMECOUNT = Component.translatable("label.atlasviewer.spriteframes");
    private static final Component LABEL_INTERPOLATED = Component.translatable("label.atlasviewer.spriteinterpolated");
    private static final Component LABEL_FRAMETIME = Component.translatable("label.atlasviewer.spriteframetime");
    private static final Component[] LABELS = { LABEL_NAME, LABEL_WIDTH, LABEL_HEIGHT, LABEL_ANIMATED, LABEL_FRAMECOUNT, LABEL_INTERPOLATED };
    private static final Component VALUE_TRUE = Component.translatable("value.atlasviewer.true");
    private static final Component VALUE_FALSE = Component.translatable("value.atlasviewer.false");
    private static final Component VALUE_FRAMETIME_MIXED = Component.translatable("value.atlasviewer.frametime_mixed");
    private static final Component VALUE_UNKNOWN_PACK = Component.translatable("value.atlasviewer.unknown_pack").withStyle(s -> s.withColor(0xD00000));
    private static final Component TITLE_EXPORT = Component.translatable("btn.atlasviewer.export_sprite");
    private static final Component TITLE_EXPORT_MIPPED = Component.translatable("btn.atlasviewer.export_mipped_sprite");
    private static final Component MSG_EXPORT_DETAILS = Component.translatable("msg.atlasviewer.export_sprite.detail");
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_sprite_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_sprite_error");
    private static final ClientTooltipPositioner PACK_LIST_POSITIONER = new FixedTooltipPositioner();
    private static final int WIDTH = 400;
    private static final int HEIGHT = 193;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 12;
    private static final int EXPORT_WIDTH = 120;
    private static final int EXPORT_HEIGHT = 20;
    private static final int MIP_LEVEL_WIDTH = 120;
    private static final int MIP_LEVEL_HEIGHT = 20;
    private static final int CLOSE_SIZE = 12;

    private final TextureAtlas atlas;
    private final TextureAtlasSprite sprite;
    private final SpriteContents contents;
    private final List<String> sourceNames;
    private final String primarySource;
    private final SpriteContents.AnimatedTexture animation;
    private final int animFrameTime;
    private int xLeft;
    private int yTop;
    private int labelLen = 0;
    private int valueX;
    private Button btnExport;
    private Button btnExportMipped;
    private List<ClientTooltipComponent> sourceNameTooltip;
    private int currentMipLevel;

    public SpriteInfoScreen(TextureAtlas atlas, TextureAtlasSprite sprite, int currentMipLevel)
    {
        super(TITLE);
        this.atlas = atlas;
        this.sprite = sprite;
        this.contents = sprite.contents();
        this.sourceNames = collectSourcePackNames();
        this.primarySource = sourceNames.isEmpty() ? null : sourceNames.get(0);
        this.animation = ((AccessorSpriteContents) contents).getAnimatedTexture();
        this.animFrameTime = getAnimationFrameTime();
        this.currentMipLevel = currentMipLevel;
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);

        DiscreteSliderButton mipLevelSlider = addRenderableWidget(new DiscreteSliderButton(
                xLeft + (PADDING * 2), yTop + HEIGHT - (PADDING * 2) - MIP_LEVEL_HEIGHT,
                MIP_LEVEL_WIDTH, MIP_LEVEL_HEIGHT,
                "btn.atlasviewer.mip_level",
                currentMipLevel,
                ((AccessorTextureAtlas) atlas).getMipLevel(),
                this::selectMipLevel
        ));
        addRenderableWidget(btnExport = Button.builder(TITLE_EXPORT, this::exportSprite)
                .pos(xLeft + WIDTH - (PADDING * 2) - EXPORT_WIDTH, yTop + HEIGHT - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );
        addRenderableWidget(btnExportMipped = Button.builder(TITLE_EXPORT_MIPPED, this::exportSpriteMipped)
                .pos(xLeft + WIDTH - (PADDING * 4) - (EXPORT_WIDTH * 2), yTop + HEIGHT - (PADDING * 2) - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );

        mipLevelSlider.active = ((AccessorTextureAtlas) atlas).getMipLevel() > 0;
        btnExportMipped.active = currentMipLevel > 0;

        for (Component label : LABELS)
        {
            labelLen = Math.max(labelLen, font.width(label));
        }
        valueX = LABEL_X + labelLen + PADDING;

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));

        sourceNameTooltip = makeTooltipList();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(graphics);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(graphics.pose(), xLeft, yTop, 0, WIDTH, HEIGHT, AtlasScreen.BACKGROUND);

        graphics.drawString(font, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040, false);

        boolean animated = animation != null;

        graphics.drawString(font, LABEL_NAME, xLeft + LABEL_X, yTop + SPRITE_Y, 0x404040, false);
        graphics.drawString(font, LABEL_WIDTH, xLeft + LABEL_X, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, LABEL_HEIGHT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        graphics.drawString(font, LABEL_SOURCE, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        graphics.drawString(font, LABEL_ANIMATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040, false);
        if (animated)
        {
            graphics.drawString(font, LABEL_FRAMECOUNT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040, false);
            graphics.drawString(font, LABEL_INTERPOLATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040, false);
            graphics.drawString(font, LABEL_FRAMETIME, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040, false);
        }

        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        TextLine name = TextLine.of(contents.name().toString(), font, maxValueLen);
        graphics.drawString(font, name.text(), xLeft + valueX, yTop + SPRITE_Y, 0x404040, false);
        graphics.drawString(font, contents.width() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040, false);
        graphics.drawString(font, contents.height() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040, false);
        Component primSrcName = primarySource != null && !primarySource.isEmpty() ? TextLine.of(primarySource, font, maxValueLen).text() : VALUE_UNKNOWN_PACK;
        graphics.drawString(font, primSrcName, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040, false);
        graphics.drawString(font, animated ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 4), animated ? 0x00D000 : 0xD00000, false);
        if (animated)
        {
            int frames = ((AccessorSpriteContents) contents).callGetFrameCount();
            graphics.drawString(font, String.valueOf(frames), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040, false);
            boolean interp = ((AccessorAnimatedTexture) animation).getInterpolateFrames();
            graphics.drawString(font, interp ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), interp ? 0x00D000 : 0xD00000, false);

            if (animFrameTime == -1)
            {
                graphics.drawString(font, VALUE_FRAMETIME_MIXED, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040, false);
            }
            else
            {
                graphics.drawString(font, animFrameTime + (animFrameTime == 1 ? " tick" : " ticks"), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040, false);
            }
        }

        float scale = 128F / Math.max(contents.width(), contents.height());

        RenderSystem.setShaderTexture(0, AtlasScreen.CHECKER_LOC);
        ClientUtils.drawNineSliceTexture(
                graphics.pose(),
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                AtlasScreen.CHECKER
        );

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();
        AtlasScreen.setAtlasMipLevel(atlas, currentMipLevel);
        graphics.blit(
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                sprite
        );
        AtlasScreen.setAtlasMipLevel(atlas, 0);
        RenderSystem.disableBlend();

        if (btnExport.isHovered())
        {
            setTooltipForNextRenderPass(MSG_EXPORT_DETAILS);
        }
        else if (btnExportMipped.active && btnExportMipped.isHovered())
        {
            setTooltipForNextRenderPass(Component.translatable("msg.atlasviewer.export_mipped_atlas.detail", currentMipLevel));
        }

        super.render(graphics, mouseX, mouseY, partialTick);

        int xRight = xLeft + valueX + font.width(name.text());
        int yBottom = yTop + SPRITE_Y + font.lineHeight;
        if (name.capped() && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y && mouseY <= yBottom)
        {
            graphics.renderTooltip(font, name.fullText(), mouseX, mouseY);
        }

        xRight = xLeft + valueX + font.width(primSrcName);
        yBottom = yTop + SPRITE_Y + (LINE_HEIGHT * 3) + font.lineHeight;
        if (!sourceNames.isEmpty() && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y + (LINE_HEIGHT * 3) && mouseY <= yBottom)
        {
            int x = xLeft + valueX;
            graphics.renderTooltipInternal(font, sourceNameTooltip, x, yTop + SPRITE_Y + (LINE_HEIGHT * 3), PACK_LIST_POSITIONER);
        }
    }

    private List<String> collectSourcePackNames()
    {
        ResourceLocation name = contents.name();
        ResourceLocation loc = new ResourceLocation(name.getNamespace(), "textures/" + name.getPath() + ".png");
        List<Resource> resources = Minecraft.getInstance().getResourceManager().getResourceStack(loc);
        List<String> sources = resources.stream()
                .map(Resource::source)
                .flatMap(pack -> getPackIds(pack, loc))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.reverse(sources);
        return sources;
    }

    private static Stream<String> getPackIds(PackResources pack, ResourceLocation loc)
    {
        if (pack instanceof DelegatingPackResources delPack)
        {
            List<PackResources> delegates = ObfuscationReflectionHelper.getPrivateValue(
                    DelegatingPackResources.class,
                    delPack,
                    "delegates"
            );
            Objects.requireNonNull(delegates);
            return delegates.stream()
                    .filter(d -> d.getResource(PackType.CLIENT_RESOURCES, loc) != null)
                    .map(PackResources::packId)
                    .map(id -> "\"" + pack.packId() + "\" -> \"" + (id.isEmpty() ? " " : id) + "\"");
        }
        return Stream.of(pack.packId());
    }

    private List<ClientTooltipComponent> makeTooltipList()
    {
        if (sourceNames.isEmpty())
        {
            return List.of();
        }

        List<Component> lines = sourceNames.stream()
                .map(Component::literal)
                .map(Component.class::cast)
                .toList();

        int maxWidth = Math.min(
                lines.stream().mapToInt(font::width).max().orElseThrow(),
                width - xLeft - valueX - 4 - PADDING
        );
        TooltipSeparator seperator = new TooltipSeparator(maxWidth, 0xFFFFFFFF, false);

        MutableBoolean first = new MutableBoolean(true);
        List<ClientTooltipComponent> components = lines.stream()
                .flatMap(line ->
                {
                    List<ClientTooltipComponent> lineComponents = font.split(line, maxWidth)
                            .stream()
                            .map(ClientTooltipComponent::create)
                            .collect(Collectors.toCollection(ArrayList::new));

                    if (first.booleanValue() && lineComponents.size() == 1)
                    {
                        lineComponents.add(new TooltipSeparator(maxWidth, 0xFFFFFFFF, true));
                    }
                    else
                    {
                        lineComponents.add(seperator);
                    }
                    first.setFalse();
                    return lineComponents.stream();
                })
                .collect(Collectors.toCollection(ArrayList::new));
        components.remove(components.size() - 1);
        return components;
    }

    private int getAnimationFrameTime()
    {
        if (animation == null)
        {
            return 0;
        }

        List<SpriteContents.FrameInfo> frames = ((AccessorAnimatedTexture) animation).getFrames();

        int first = ((AccessorFrameInfo) frames.get(0)).getTime();
        for (SpriteContents.FrameInfo frame : frames)
        {
            if (((AccessorFrameInfo) frame).getTime() != first)
            {
                // No point in displaying a frame time when it's different for some frames
                return -1;
            }
        }

        return first;
    }

    private void selectMipLevel(int level)
    {
        currentMipLevel = level;
        btnExportMipped.active = level > 0;
    }

    private void exportSprite(Button btn)
    {
        exportSprite(0);
    }

    private void exportSpriteMipped(Button btn)
    {
        exportSprite(currentMipLevel);
    }

    private void exportSprite(int mipLevel)
    {
        try
        {
            NativeImage image = contents.byMipLevel[mipLevel];
            AtlasScreen.exportNativeImage(image, contents.name(), "sprite", false, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting sprite", e);
            Minecraft.getInstance().pushGuiLayer(MessageScreen.error(List.of(
                    MSG_EXPORT_ERROR,
                    Component.literal(e.getMessage())
            )));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (button == GLFW.GLFW_MOUSE_BUTTON_1 && (mouseX < xLeft || mouseY < yTop || mouseX > (xLeft + WIDTH) || mouseY > (yTop + HEIGHT)))
        {
            onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
