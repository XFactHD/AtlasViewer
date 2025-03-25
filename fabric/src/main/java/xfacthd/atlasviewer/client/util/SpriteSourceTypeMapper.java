package xfacthd.atlasviewer.client.util;

import com.mojang.serialization.MapCodec;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.client.renderer.texture.atlas.sources.SourceFilter;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.AccessorSpriteSources;

import java.util.*;
import java.util.stream.Collectors;

public final class SpriteSourceTypeMapper
{
    private static final Map<MapCodec<? extends SpriteSource>, String> NAMES = new HashMap<>();
    private static final boolean USING_INTERMEDIARY = FabricLoader.getInstance()
            .getMappingResolver()
            .getCurrentRuntimeNamespace()
            .equals("intermediary");

    public static String getSpriteSourceName(SpriteSource source)
    {
        String name = source.getClass().getName();
        if (USING_INTERMEDIARY)
        {
            return NAMES.getOrDefault(source.codec(), name);
        }
        return name;
    }

    public static void init()
    {
        NAMES.put(SingleFile.MAP_CODEC, "net.minecraft.client.renderer.texture.atlas.sources.SingleFile");
        NAMES.put(DirectoryLister.MAP_CODEC, "net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister");
        NAMES.put(SourceFilter.MAP_CODEC, "net.minecraft.client.renderer.texture.atlas.sources.SourceFilter");
        NAMES.put(Unstitcher.MAP_CODEC, "net.minecraft.client.renderer.texture.atlas.sources.Unstitcher");
        NAMES.put(PalettedPermutations.MAP_CODEC, "net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations");

        if (FabricLoader.getInstance().isDevelopmentEnvironment())
        {
            String missing = AccessorSpriteSources.atlasviewer$getTypes()
                    .atlasviewer$stream()
                    .filter(e -> e.getKey().getNamespace().equals("minecraft"))
                    .filter(e -> !NAMES.containsKey(e.getValue()))
                    .map(Map.Entry::getKey)
                    .map(ResourceLocation::toString)
                    .collect(Collectors.joining(", "));

            if (!missing.isEmpty())
            {
                throw new IllegalStateException("Missing mappings for one or more SpriteSource types: " + missing);
            }
        }
    }



    private SpriteSourceTypeMapper() { }
}
