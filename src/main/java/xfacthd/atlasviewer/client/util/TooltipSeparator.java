package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;

public class TooltipSeparator implements ClientTooltipComponent
{
    private final int width;
    private final int height;
    private final int yOff;
    private final int color;

    public TooltipSeparator(int width, int color, boolean first)
    {
        this.width = width;
        this.color = color;
        this.height = first ? 5 : 7;
        this.yOff = first ? 0 : 2;
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, PoseStack poseStack, ItemRenderer itemRenderer, int blitOffset)
    {
        GuiComponent.fill(poseStack, mouseX, mouseY + yOff, mouseX + width + 1, mouseY + yOff + 1, color);
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public int getWidth(Font font)
    {
        return width;
    }
}
