package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.NineSlice;

public class CloseButton extends Button
{
    private static final Component TITLE = Component.literal("x");
    private static final NineSlice[] SLICES = new NineSlice[] {
            new NineSlice(0, 46, 200, 20, 256, 256, 3),
            new NineSlice(0, 66, 200, 20, 256, 256, 3),
            new NineSlice(0, 86, 200, 20, 256, 256, 3)
    };

    public CloseButton(int x, int y, Screen owner)
    {
        super(x, y, 12, 12, TITLE, btn -> owner.onClose(), Button.DEFAULT_NARRATION);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        NineSlice slice = SLICES[getTextureY()];
        ClientUtils.drawNineSliceTexture(graphics.pose(), getX(), getY(), 0, width, height, slice);

        Font font = Minecraft.getInstance().font;
        int fgColor = getFGColor() | 0xFF000000;
        graphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 10) / 2, fgColor);
    }

    private int getTextureY() {
        if (!active)
        {
            return 0;
        }
        else if (isHoveredOrFocused())
        {
            return 2;
        }
        return 1;
    }
}
