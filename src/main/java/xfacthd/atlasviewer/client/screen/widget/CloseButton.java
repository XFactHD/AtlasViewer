package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.client.util.ClientUtils;

public class CloseButton extends Button
{
    private static final Component TITLE = Component.literal("x");

    private final Screen owner;

    public CloseButton(int x, int y, Screen owner)
    {
        super(x, y, 12, 12, TITLE, btn -> owner.onClose(), Button.DEFAULT_NARRATION);
        this.owner = owner;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        int vY = 46 + getYImage(isHoveredOrFocused()) * 20;
        ClientUtils.drawNineSliceTexture(owner, poseStack, getX(), getY(), width, height, 0, vY, 200, 20, 256, 256, 3);

        Font font = Minecraft.getInstance().font;
        int fgColor = getFGColor() | 0xFF000000;
        drawCenteredString(poseStack, font, getMessage(), getX() + width / 2, getY() + (height - 10) / 2, fgColor);
    }
}