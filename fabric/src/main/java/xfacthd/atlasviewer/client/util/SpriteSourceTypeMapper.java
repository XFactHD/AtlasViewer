package xfacthd.atlasviewer.client.util;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.Collectors;

public final class SpriteSourceTypeMapper
{
    private static final Map<SpriteSourceType, String> NAMES = new HashMap<>();
    private static final boolean USING_INTERMEDIARY = FabricLoader.getInstance()
            .getMappingResolver()
            .getCurrentRuntimeNamespace()
            .equals("intermediary");

    public static String getSpriteSourceName(SpriteSource source)
    {
        String name = source.getClass().getName();
        if (USING_INTERMEDIARY)
        {
            return NAMES.getOrDefault(source.type(), name);
        }
        return name;
    }

    public static void init()
    {
        NAMES.put(SpriteSources.SINGLE_FILE, "net.minecraft.client.renderer.texture.atlas.sources.SingleFile");
        NAMES.put(SpriteSources.DIRECTORY, "net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister");
        NAMES.put(SpriteSources.FILTER, "net.minecraft.client.renderer.texture.atlas.sources.SourceFilter");
        NAMES.put(SpriteSources.UNSTITCHER, "net.minecraft.client.renderer.texture.atlas.sources.Unstitcher");
        NAMES.put(SpriteSources.PALETTED_PERMUTATIONS, "net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations");

        if (FabricLoader.getInstance().isDevelopmentEnvironment())
        {
            String missing = SpriteSources.TYPES.entrySet()
                    .stream()
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
