package xfacthd.atlasviewer.platform.services;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;

import java.nio.file.Path;

public interface IPlatformHelper
{
    boolean isDevelopmentEnvironment();

    Path getGameDir();

    void fireSpriteSourceDetailsEvent();

    void pushScreenLayer(Screen screen);

    void popScreenLayer();

    String getSpriteSourceName(SpriteSource source);

    String getSpriteSourceSimpleName(SpriteSource source);
}