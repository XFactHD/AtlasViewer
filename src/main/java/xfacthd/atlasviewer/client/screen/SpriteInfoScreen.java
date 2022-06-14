package xfacthd.atlasviewer.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.util.ClientUtils;

public class SpriteInfoScreen extends Screen
{
    private static final Component TITLE = Component.translatable("title.atlasviewer.spriteinfo");
    private static final Component LABEL_NAME = Component.translatable("label.atlasviewer.spritename");
    private static final Component LABEL_WIDTH = Component.translatable("label.atlasviewer.spritewidth");
    private static final Component LABEL_HEIGHT = Component.translatable("label.atlasviewer.spriteheight");
    private static final Component LABEL_ANIMATED = Component.translatable("label.atlasviewer.spriteanimated");
    private static final Component VALUE_TRUE = Component.translatable("value.atlasviewer.true");
    private static final Component VALUE_FALSE = Component.translatable("value.atlasviewer.false");
    private static final int WIDTH = 400;
    private static final int HEIGHT = 163;
    private static final int PADDING = 5;
    private static final int LABEL_X = 148;
    private static final int SPRITE_Y = 25;
    private static final int LINE_HEIGHT = 15;

    private final TextureAtlasSprite sprite;
    private int xLeft;
    private int yTop;

    public SpriteInfoScreen(TextureAtlasSprite sprite)
    {
        super(TITLE);
        this.sprite = sprite;
    }

    @Override
    protected void init()
    {
        xLeft = (width / 2) - (WIDTH / 2);
        yTop = (height / 2) - (HEIGHT / 2);
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        renderBackground(poseStack);

        RenderSystem.setShaderTexture(0, new ResourceLocation("minecraft", "textures/gui/demo_background.png"));
        ClientUtils.drawNineSliceTexture(this, poseStack, xLeft, yTop, WIDTH, HEIGHT, 248, 166, 4);

        font.draw(poseStack, title, xLeft + (PADDING * 2), yTop + (PADDING * 2), 0x404040);

        int labelLen = Math.max(Math.max(font.width(LABEL_NAME), font.width(LABEL_WIDTH)), Math.max(font.width(LABEL_HEIGHT), font.width(LABEL_ANIMATED)));

        font.draw(poseStack, LABEL_NAME, xLeft + LABEL_X, yTop + 25, 0x404040);
        font.draw(poseStack, LABEL_WIDTH, xLeft + LABEL_X, yTop + 40, 0x404040);
        font.draw(poseStack, LABEL_HEIGHT, xLeft + LABEL_X, yTop + 55, 0x404040);
        font.draw(poseStack, LABEL_ANIMATED, xLeft + LABEL_X, yTop + 70, 0x404040);

        int valueX = LABEL_X + labelLen + PADDING;
        String name = sprite.getName().toString();
        boolean cappedName = false;
        if (valueX + font.width(name) > (WIDTH - (PADDING * 2)))
        {
            name = font.plainSubstrByWidth(name, WIDTH - valueX - (PADDING * 2)) + "...";
            cappedName = true;
        }

        boolean animated = sprite.getAnimationTicker() != null;
        font.draw(poseStack, name, xLeft + valueX, yTop + SPRITE_Y, 0x404040);
        font.draw(poseStack, sprite.getWidth() + "px", xLeft + valueX, yTop + SPRITE_Y + LINE_HEIGHT, 0x404040);
        font.draw(poseStack, sprite.getHeight() + "px", xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 2), 0x404040);
        font.draw(poseStack, animated ? VALUE_TRUE : VALUE_FALSE, xLeft + valueX, yTop + SPRITE_Y + (LINE_HEIGHT * 3), animated ? 0x00D000 : 0xD00000);

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
            renderTooltip(poseStack, Component.literal(sprite.getName().toString()), mouseX, mouseY);
        }
    }
}
