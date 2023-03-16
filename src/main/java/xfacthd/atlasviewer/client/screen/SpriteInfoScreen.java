package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_sprite_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_sprite_error");
    private static final ClientTooltipPositioner PACK_LIST_POSITIONER = new FixedTooltipPositioner();
    private static final int WIDTH = 400;
    private static final int HEIGHT = 163;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 15;
    private static final int EXPORT_WIDTH = 100;
    private static final int EXPORT_HEIGHT = 20;
    private static final int CLOSE_SIZE = 12;

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
    private List<ClientTooltipComponent> sourceNameTooltip;

    public SpriteInfoScreen(TextureAtlasSprite sprite)
    {
        super(TITLE);
        this.sprite = sprite;
        this.contents = sprite.contents();
        this.sourceNames = collectSourcePackNames();
        this.primarySource = sourceNames.isEmpty() ? null : sourceNames.get(0);
        this.animation = ((AccessorSpriteContents) contents).getAnimatedTexture();
        this.animFrameTime = getAnimationFrameTime();
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);

        addRenderableWidget(Button.builder(TITLE_EXPORT, this::exportSprite)
                .pos(xLeft + WIDTH - PADDING - EXPORT_WIDTH, yTop + HEIGHT - PADDING - EXPORT_HEIGHT)
                .size(EXPORT_WIDTH, EXPORT_HEIGHT)
                .build()
        );

        for (Component label : LABELS)
        {
            labelLen = Math.max(labelLen, font.width(label));
        }
        valueX = LABEL_X + labelLen + PADDING;

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));

        sourceNameTooltip = makeTooltipList();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, AtlasScreen.BACKGROUND_LOC);
        ClientUtils.drawNineSliceTexture(poseStack, xLeft, yTop, 0, WIDTH, HEIGHT, AtlasScreen.BACKGROUND);

        font.draw(poseStack, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040);

        boolean animated = animation != null;

        font.draw(poseStack, LABEL_NAME, xLeft + LABEL_X, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, LABEL_WIDTH, xLeft + LABEL_X, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, LABEL_HEIGHT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        font.draw(poseStack, LABEL_SOURCE, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040);
        font.draw(poseStack, LABEL_ANIMATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040);
        if (animated)
        {
            font.draw(poseStack, LABEL_FRAMECOUNT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040);
            font.draw(poseStack, LABEL_INTERPOLATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040);
            font.draw(poseStack, LABEL_FRAMETIME, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040);
        }

        int maxValueLen = WIDTH - (PADDING * 2) - valueX;

        TextLine name = TextLine.of(contents.name().toString(), font, maxValueLen);
        font.draw(poseStack, name.text(), xLeft + valueX, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, contents.width() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, contents.height() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        Component primSrcName = primarySource != null ? TextLine.of(primarySource, font, maxValueLen).text() : VALUE_UNKNOWN_PACK;
        font.draw(poseStack, primSrcName, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040);
        font.draw(poseStack, animated ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 4), animated ? 0x00D000 : 0xD00000);
        if (animated)
        {
            int frames = ((AccessorSpriteContents) contents).callGetFrameCount();
            font.draw(poseStack, String.valueOf(frames), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040);
            boolean interp = ((AccessorAnimatedTexture) animation).getInterpolateFrames();
            font.draw(poseStack, interp ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), interp ? 0x00D000 : 0xD00000);

            if (animFrameTime == -1)
            {
                font.draw(poseStack, VALUE_FRAMETIME_MIXED, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040);
            }
            else
            {
                font.draw(poseStack, animFrameTime + (animFrameTime == 1 ? " tick" : " ticks"), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 7), 0x404040);
            }
        }

        float scale = 128F / Math.max(contents.width(), contents.height());

        RenderSystem.setShaderTexture(0, AtlasScreen.CHECKER_LOC);
        ClientUtils.drawNineSliceTexture(
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                AtlasScreen.CHECKER
        );

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();
        blit(
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                0,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                sprite
        );
        RenderSystem.disableBlend();

        super.render(poseStack, mouseX, mouseY, partialTick);

        int xRight = xLeft + valueX + font.width(name.text());
        int yBottom = yTop + SPRITE_Y + font.lineHeight;
        if (name.capped() && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y && mouseY <= yBottom)
        {
            renderTooltip(poseStack, name.fullText(), mouseX, mouseY);
        }

        xRight = xLeft + valueX + font.width(primSrcName);
        yBottom = yTop + SPRITE_Y + (LINE_HEIGHT * 3) + font.lineHeight;
        if (!sourceNames.isEmpty() && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y + (LINE_HEIGHT * 3) && mouseY <= yBottom)
        {
            int x = xLeft + valueX - 12;
            renderTooltipInternal(poseStack, sourceNameTooltip, x, yTop + SPRITE_Y + (LINE_HEIGHT * 3) + 12, PACK_LIST_POSITIONER);
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
                .map(s -> "" + s)
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

    private void exportSprite(Button btn)
    {
        NativeImage image = contents.byMipLevel[0];
        try
        {
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
