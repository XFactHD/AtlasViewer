package xfacthd.atlasviewer.platform.services;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public interface IPlatformHelper
{
    boolean isDevelopmentEnvironment();

    Path getGameDir();

    void fireSpriteSourceDetailsEvent();

    void pushScreenLayer(Screen screen);

    void popScreenLayer();

    @Nullable
    ScreenRectangle peekScissorState(GuiGraphics graphics);

    void submitCustomGuiRenderState(GuiGraphics graphics, GuiElementRenderState renderState);

    String getSpriteSourceName(SpriteSource source);

    String getSpriteSourceSimpleName(SpriteSource source);

    void registerPlatformSpecificBuiltInSpriteSourceDetails();
}
