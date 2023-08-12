package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.AtlasViewer;

public final class IndicatorButton extends Button
{
    private static final ResourceLocation INDICATOR_TEXTURE = new ResourceLocation(AtlasViewer.MOD_ID, "textures/gui/indicator.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int INDICATOR_SIZE = 13;

    private boolean checked = false;

    public IndicatorButton(int x, int y, int w, int h, Component text, @Nullable IndicatorButton prev, OnPress onPress)
    {
        super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
        if (prev != null)
        {
            this.checked = prev.checked;
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        int x = getX() + width - INDICATOR_SIZE - 3;
        int y = getY() + 3;
        int texY = checked ? INDICATOR_SIZE : 0;
        renderTexture(graphics, INDICATOR_TEXTURE, x, y, 0, texY, 0, INDICATOR_SIZE, INDICATOR_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color)
    {
        int minX = getX() + 2;
        int maxX = getX() + getWidth() - INDICATOR_SIZE - 6;
        renderScrollingString(graphics, font, getMessage(), minX, getY(), maxX, getY() + getHeight(), color);
    }

    @Override
    public void onPress()
    {
        checked = !checked;
        super.onPress();
    }

    public boolean isChecked()
    {
        return checked;
    }

    public void setChecked(boolean checked)
    {
        this.checked = checked;
    }
}
