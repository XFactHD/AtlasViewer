package xfacthd.atlasviewer.platform;

import com.google.common.base.Preconditions;
import net.fabricmc.fabric.impl.resource.loader.GroupResourcePack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import xfacthd.atlasviewer.AtlasViewer;
import net.fabricmc.loader.api.FabricLoader;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.client.util.SpriteSourceTypeMapper;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public final class FabricPlatformHelper implements IPlatformHelper
{
    private static final MethodHandle MH_GRP_GETPACKS = makeChildPackGetterMH();

    @Override
    public boolean isDevelopmentEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDir()
    {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public void fireSpriteSourceDetailsEvent()
    {
        RegisterSpriteSourceDetailsEvent.EVENT.invoker().accept(
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        );
    }

    @Override
    public void pushScreenLayer(Screen screen)
    {
        ScreenStacker.pushScreenLayer(screen);
    }

    @Override
    public void popScreenLayer()
    {
        ScreenStacker.popScreenLayer();
    }

    @Override
    @SuppressWarnings({ "UnstableApiUsage", "unchecked" })
    public Stream<String> getPackIDs(PackResources pack, ResourceLocation loc)
    {
        try
        {
            if (pack instanceof GroupResourcePack groupPack)
            {
                Preconditions.checkNotNull(
                        MH_GRP_GETPACKS,
                        "Accessor for children of grouped pack not available, see log for earlier errors"
                );

                List<PackResources> children = (List<PackResources>) MH_GRP_GETPACKS.invoke(groupPack);
                return children.stream()
                        .filter(d -> d.getResource(PackType.CLIENT_RESOURCES, loc) != null)
                        .map(PackResources::packId)
                        .map(id -> "\"" + pack.packId() + "\" -> \"" + (id.isEmpty() ? " " : id) + "\"");
            }
        }
        catch (Throwable t)
        {
            AtlasViewer.LOGGER.error("Encountered an error when trying to flatten grouped mod resources", t);
            return Stream.of("[ERROR]");
        }
        return Stream.of(pack.packId());
    }

    @SuppressWarnings("UnstableApiUsage")
    private static MethodHandle makeChildPackGetterMH()
    {
        try
        {
            Field field = GroupResourcePack.class.getDeclaredField("packs");
            field.setAccessible(true);
            return MethodHandles.publicLookup().unreflectGetter(field);
        }
        catch (Throwable t)
        {
            AtlasViewer.LOGGER.error("Encountered an error when trying to make getter MethodHandle for child packs of GroupResourcePack", t);
            return null;
        }
    }

    @Override
    public String getSpriteSourceName(SpriteSource source)
    {
        return SpriteSourceTypeMapper.getSpriteSourceName(source);
    }

    @Override
    public String getSpriteSourceSimpleName(SpriteSource source)
    {
        String name = getSpriteSourceName(source);
        int lastPeriod = name.lastIndexOf('.');
        if (lastPeriod > -1)
        {
            name = name.substring(lastPeriod + 1);
        }
        return name;
    }
}
