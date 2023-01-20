package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.mixin.*;
import xfacthd.atlasviewer.client.screen.widget.CloseButton;
import xfacthd.atlasviewer.client.util.ClientUtils;

import java.io.IOException;
import java.util.List;

public class SpriteInfoScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.spriteinfo");
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.spritename");
    private static final Component LABEL_WIDTH = Component.translatable("label.atlasviewer.spritewidth");
    private static final Component LABEL_HEIGHT = Component.translatable("label.atlasviewer.spriteheight");
    private static final Component LABEL_ANIMATED = Component.translatable("label.atlasviewer.spriteanimated");
    private static final Component LABEL_FRAMECOUNT = Component.translatable("label.atlasviewer.spriteframes");
    private static final Component LABEL_INTERPOLATED = Component.translatable("label.atlasviewer.spriteinterpolated");
    private static final Component LABEL_FRAMETIME = Component.translatable("label.atlasviewer.spriteframetime");
    private static final Component[] LABELS = { LABEL_NAME, LABEL_WIDTH, LABEL_HEIGHT, LABEL_ANIMATED, LABEL_FRAMECOUNT, LABEL_INTERPOLATED };
    private static final Component VALUE_TRUE = Component.translatable("value.atlasviewer.true");
    private static final Component VALUE_FALSE = Component.translatable("value.atlasviewer.false");
    private static final Component VALUE_FRAMETIME_MIXED = Component.translatable("value.atlasviewer.frametime_mixed");
    private static final Component TITLE_EXPORT = Component.translatable("btn.atlasviewer.export_sprite");
    private static final Component MSG_EXPORT_SUCCESS = Component.translatable("msg.atlasviewer.export_sprite_success");
    private static final Component MSG_EXPORT_ERROR = Component.translatable("msg.atlasviewer.export_sprite_error");
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
    private final SpriteContents.AnimatedTexture animation;
    private final int animFrameTime;
    private int xLeft;
    private int yTop;
    private int labelLen = 0;

    public SpriteInfoScreen(TextureAtlasSprite sprite)
    {
        super(TITLE);
        this.sprite = sprite;
        this.contents = sprite.contents();
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

        addRenderableWidget(new CloseButton(xLeft + WIDTH - PADDING - CLOSE_SIZE, yTop + PADDING, this));
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/demo_background.png"));
        ClientUtils.drawNineSliceTexture(this, poseStack, xLeft, yTop, WIDTH, HEIGHT, 248, 166, 4);

        font.draw(poseStack, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040);

        boolean animated = animation != null;

        font.draw(poseStack, LABEL_NAME, xLeft + LABEL_X, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, LABEL_WIDTH, xLeft + LABEL_X, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, LABEL_HEIGHT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        font.draw(poseStack, LABEL_ANIMATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 3), 0x404040);
        if (animated)
        {
            font.draw(poseStack, LABEL_FRAMECOUNT, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040);
            font.draw(poseStack, LABEL_INTERPOLATED, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 5), 0x404040);
            font.draw(poseStack, LABEL_FRAMETIME, xLeft + LABEL_X, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040);
        }

        int valueX = LABEL_X + labelLen + PADDING;
        String name = contents.name().toString();
        boolean cappedName = false;
        if (valueX + font.width(name) > (WIDTH - (PADDING * 2)))
        {
            name = font.plainSubstrByWidth(name, WIDTH - valueX - (PADDING * 2)) + "...";
            cappedName = true;
        }

        font.draw(poseStack, name, xLeft + valueX, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, contents.width() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, contents.height() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        font.draw(poseStack, animated ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), animated ? 0x00D000 : 0xD00000);
        if (animated)
        {
            int frames = ((AccessorSpriteContents) contents).callGetFrameCount();
            font.draw(poseStack, String.valueOf(frames), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040);
            boolean interp = ((AccessorAnimatedTexture) animation).getInterpolateFrames();
            font.draw(poseStack, interp ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 5), interp ? 0x00D000 : 0xD00000);

            if (animFrameTime == -1)
            {
                font.draw(poseStack, VALUE_FRAMETIME_MIXED, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040);
            }
            else
            {
                font.draw(poseStack, animFrameTime + (animFrameTime == 1 ? " tick" : " ticks"), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040);
            }
        }

        float scale = 128F / Math.max(contents.width(), contents.height());

        RenderSystem.setShaderTexture(0, new ResourceLocation(AtlasViewer.MOD_ID, "textures/gui/checker.png"));
        ClientUtils.drawNineSliceTexture(
                this,
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                256,
                256,
                0
        );

        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        RenderSystem.enableBlend();
        blit(
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                getBlitOffset(),
                (int)(contents.width() * scale),
                (int)(contents.height() * scale),
                sprite
        );
        RenderSystem.disableBlend();

        super.render(poseStack, mouseX, mouseY, partialTick);

        int xRight = xLeft + valueX + font.width(name);
        if (cappedName && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y && mouseY <= yTop + SPRITE_Y + font.lineHeight)
        {
            renderTooltip(poseStack, Component.literal(contents.name().toString()), mouseX, mouseY);
        }
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
