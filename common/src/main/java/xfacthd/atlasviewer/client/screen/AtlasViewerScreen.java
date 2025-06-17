package xfacthd.atlasviewer.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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
        if (this == Objects.requireNonNull(minecraft).screen)
        {
            // Only render blur for the top-most screen
            super.renderBlurredBackground(graphics);
        }
    }
}
