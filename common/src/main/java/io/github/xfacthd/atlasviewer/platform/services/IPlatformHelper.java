package io.github.xfacthd.atlasviewer.platform.services;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.atlasviewer.client.util.WrappedSpriteSource;

import java.nio.file.Path;

public interface IPlatformHelper {
    boolean isDevelopmentEnvironment();

    Path getGameDir();

    void fireSpriteSourceDetailsEvent();

    void pushScreenLayer(Screen screen);

    void popScreenLayer();

    @Nullable ScreenRectangle peekScissorState(GuiGraphicsExtractor graphics);

    void submitCustomGuiRenderState(GuiGraphicsExtractor graphics, GuiElementRenderState renderState);

    void registerPlatformSpecificBuiltInSpriteSourceDetails();

    default SpriteSource wrapSpriteSource(SpriteSource original) {
        return new WrappedSpriteSource(original);
    }

    void setTooltip(Font font, GuiGraphicsExtractor graphics, Component component, int mouseX, int mouseY);
}
