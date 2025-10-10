package xfacthd.atlasviewer.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.platform.Services;

import java.util.Objects;

public abstract class AtlasViewerScreen extends Screen
{
    protected AtlasViewerScreen(Component title)
    {
        super(title);
    }

    @Override
    protected void renderBlurredBackground(GuiGraphics graphics)
    {
        if (this == minecraft().screen)
        {
            // Only render blur for the top-most screen
            super.renderBlurredBackground(graphics);
        }
    }

    protected void setTooltipForNextFrame(GuiGraphics graphics, Component component, int mouseX, int mouseY)
    {
        Services.PLATFORM.setTooltip(font, graphics, component, mouseX, mouseY);
    }

    protected final Minecraft minecraft()
    {
        return Objects.requireNonNull(minecraft);
    }
}
