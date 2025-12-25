package xfacthd.atlasviewer.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
    public void extractImage(Font font, int mouseX, int mouseY, int width, int height, GuiGraphicsExtractor graphics)
    {
        graphics.fill(mouseX, mouseY + yOff, mouseX + this.width + 1, mouseY + yOff + 1, color);
    }

    @Override
    public int getHeight(Font font)
    {
        return height;
    }

    @Override
    public int getWidth(Font font)
    {
        return width;
    }
}
