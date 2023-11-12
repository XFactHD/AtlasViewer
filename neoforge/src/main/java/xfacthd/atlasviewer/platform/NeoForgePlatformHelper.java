package xfacthd.atlasviewer.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.resource.DelegatingPackResources;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;

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
    public Stream<String> getPackIDs(PackResources pack, ResourceLocation loc)
    {
        if (pack instanceof DelegatingPackResources delPack)
        {
            return Objects.requireNonNull(delPack.getChildren())
                    .stream()
                    .filter(d -> d.getResource(PackType.CLIENT_RESOURCES, loc) != null)
                    .map(PackResources::packId)
                    .map(id -> "\"" + pack.packId() + "\" -> \"" + (id.isEmpty() ? " " : id) + "\"");
        }
        return Stream.of(pack.packId());
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