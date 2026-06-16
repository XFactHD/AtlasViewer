package xfacthd.atlasviewer.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.platform.Services;

import java.util.Objects;

public abstract class AtlasViewerScreen extends Screen {
    protected AtlasViewerScreen(Component title) {
        super(title);
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
        if (this == minecraft().gui.screen()) {
            // Only render blur for the top-most screen
            super.extractBlurredBackground(graphics);
        }
    }

    protected void setTooltipForNextFrame(GuiGraphicsExtractor graphics, Component component, int mouseX, int mouseY) {
        Services.PLATFORM.setTooltip(font, graphics, component, mouseX, mouseY);
    }

    protected final Minecraft minecraft() {
        return Objects.requireNonNull(minecraft);
    }
}
