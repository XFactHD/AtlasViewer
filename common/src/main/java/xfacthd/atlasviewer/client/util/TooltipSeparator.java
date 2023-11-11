package xfacthd.atlasviewer.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public final class TooltipSeparator implements ClientTooltipComponent
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
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics graphics)
    {
        graphics.fill(mouseX, mouseY + yOff, mouseX + width + 1, mouseY + yOff + 1, color);
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
