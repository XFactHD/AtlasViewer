package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlasSprite;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlasSpriteInfo;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.ITextureAtlasSpriteInfoGetter;

import java.io.IOException;
import java.util.List;

public class SpriteInfoScreen extends Screen
{
    private static final Component TITLE = new TranslatableComponent("title.atlasviewer.spriteinfo");
    private static final Component LABEL_NAME = new TranslatableComponent("label.atlasviewer.spritename");
    private static final Component LABEL_WIDTH = new TranslatableComponent("label.atlasviewer.spritewidth");
    private static final Component LABEL_HEIGHT = new TranslatableComponent("label.atlasviewer.spriteheight");
    private static final Component LABEL_ANIMATED = new TranslatableComponent("label.atlasviewer.spriteanimated");
    private static final Component LABEL_FRAMECOUNT = new TranslatableComponent("label.atlasviewer.spriteframes");
    private static final Component LABEL_INTERPOLATED = new TranslatableComponent("label.atlasviewer.spriteinterpolated");
    private static final Component LABEL_FRAMETIME = new TranslatableComponent("label.atlasviewer.spriteframetime");
    private static final Component[] LABELS = { LABEL_NAME, LABEL_WIDTH, LABEL_HEIGHT, LABEL_ANIMATED, LABEL_FRAMECOUNT, LABEL_INTERPOLATED };
    private static final Component VALUE_TRUE = new TranslatableComponent("value.atlasviewer.true");
    private static final Component VALUE_FALSE = new TranslatableComponent("value.atlasviewer.false");
    private static final Component TITLE_EXPORT = new TranslatableComponent("btn.atlasviewer.export_sprite");
    private static final Component MSG_EXPORT_SUCCESS = new TranslatableComponent("msg.atlasviewer.export_sprite_success");
    private static final Component MSG_EXPORT_ERROR = new TranslatableComponent("msg.atlasviewer.export_sprite_error");
    private static final int WIDTH = 400;
    private static final int HEIGHT = 163;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 15;
    private static final int EXPORT_WIDTH = 100;
    private static final int EXPORT_HEIGHT = 20;

    private final TextureAtlasSprite sprite;
    private final AnimationMetadataSection animMeta;
    private int xLeft;
    private int yTop;
    private int labelLen = 0;

    public SpriteInfoScreen(TextureAtlasSprite sprite)
    {
        super(TITLE);
        this.sprite = sprite;

        TextureAtlasSprite.Info info = ((ITextureAtlasSpriteInfoGetter) sprite).getInfo();
        animMeta = ((AccessorTextureAtlasSpriteInfo) (Object)info).getMetadata();
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);

        addRenderableWidget(new Button(
                xLeft + WIDTH - PADDING - EXPORT_WIDTH,
                yTop + HEIGHT - PADDING - EXPORT_HEIGHT,
                EXPORT_WIDTH,
                EXPORT_HEIGHT,
                TITLE_EXPORT,
                this::exportSprite
        ));

        for (Component label : LABELS)
        {
            labelLen = Math.max(labelLen, font.width(label));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/demo_background.png"));
        ClientUtils.drawNineSliceTexture(this, poseStack, xLeft, yTop, WIDTH, HEIGHT, 248, 166, 4);

        font.draw(poseStack, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040);

        boolean animated = sprite.getAnimationTicker() != null;

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
        String name = sprite.getName().toString();
        boolean cappedName = false;
        if (valueX + font.width(name) > (WIDTH - (PADDING * 2)))
        {
            name = font.plainSubstrByWidth(name, WIDTH - valueX - (PADDING * 2)) + "...";
            cappedName = true;
        }

        font.draw(poseStack, name, xLeft + valueX, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, sprite.getWidth() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, sprite.getHeight() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        font.draw(poseStack, animated ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), animated ? 0x00D000 : 0xD00000);
        if (animated)
        {
            font.draw(poseStack, String.valueOf(sprite.getFrameCount()), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 4), 0x404040);
            boolean interp = animMeta.isInterpolatedFrames();
            font.draw(poseStack, interp ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 5), interp ? 0x00D000 : 0xD00000);
            int ticks = animMeta.getDefaultFrameTime();
            font.draw(poseStack, ticks + (ticks == 1 ? " tick" : " ticks"), xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 6), 0x404040);
        }

        float scale = 128F / Math.max(sprite.getWidth(), sprite.getHeight());

        RenderSystem.setShaderTexture(0, new ResourceLocation(AtlasViewer.MOD_ID, "textures/gui/checker.png"));
        ClientUtils.drawNineSliceTexture(
                this,
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                (int)(sprite.getWidth() * scale),
                (int)(sprite.getHeight() * scale),
                256,
                256,
                0
        );

        RenderSystem.setShaderTexture(0, sprite.atlas().location());
        RenderSystem.enableBlend();
        blit(
                poseStack,
                xLeft + (PADDING * 2),
                yTop + SPRITE_Y,
                getBlitOffset(),
                (int)(sprite.getWidth() * scale),
                (int)(sprite.getHeight() * scale),
                sprite
        );
        RenderSystem.disableBlend();

        super.render(poseStack, mouseX, mouseY, partialTick);

        int xRight = xLeft + valueX + font.width(name);
        if (cappedName && mouseX >= xLeft + valueX && mouseX <= xRight && mouseY >= yTop + SPRITE_Y && mouseY <= yTop + SPRITE_Y + font.lineHeight)
        {
            renderTooltip(poseStack, new TextComponent(sprite.getName().toString()), mouseX, mouseY);
        }
    }

    private void exportSprite(Button btn)
    {
        NativeImage image = ((AccessorTextureAtlasSprite) sprite).getMainImage()[0];
        try
        {
            AtlasScreen.exportNativeImage(image, sprite.getName(), "sprite", false, MSG_EXPORT_SUCCESS);
        }
        catch (IOException e)
        {
            AtlasViewer.LOGGER.error("Encountered an error while exporting sprite", e);
            Minecraft.getInstance().pushGuiLayer(MessageScreen.error(List.of(
                    MSG_EXPORT_ERROR,
                    new TextComponent(e.getMessage())
            )));
        }
    }
}
