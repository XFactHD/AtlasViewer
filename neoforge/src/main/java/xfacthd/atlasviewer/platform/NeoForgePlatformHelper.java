package xfacthd.atlasviewer.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;

public final class NeoForgePlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isDevelopmentEnvironment()
    {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getGameDir()
    {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public void fireSpriteSourceDetailsEvent()
    {
        ModLoader.get().postEvent(new RegisterSpriteSourceDetailsEvent(
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        ));
    }

    @Override
    public void pushScreenLayer(Screen screen)
    {
        Minecraft.getInstance().pushGuiLayer(screen);
    }

    @Override
    public void popScreenLayer()
    {
        Minecraft.getInstance().popGuiLayer();
    }

    @Override
    public String getSpriteSourceName(SpriteSource source)
    {
        return source.getClass().getName();
    }

    @Override
    public String getSpriteSourceSimpleName(SpriteSource source)
    {
        return source.getClass().getSimpleName();
    }
}