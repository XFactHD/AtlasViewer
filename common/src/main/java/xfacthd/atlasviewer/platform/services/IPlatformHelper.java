package xfacthd.atlasviewer.platform.services;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface IPlatformHelper
{
    boolean isDevelopmentEnvironment();

    Path getGameDir();

    void fireSpriteSourceDetailsEvent();

    void pushScreenLayer(Screen screen);

    void popScreenLayer();

    Stream<String> getPackIDs(PackResources pack, ResourceLocation loc);

    String getSpriteSourceName(SpriteSource source);

    String getSpriteSourceSimpleName(SpriteSource source);
}