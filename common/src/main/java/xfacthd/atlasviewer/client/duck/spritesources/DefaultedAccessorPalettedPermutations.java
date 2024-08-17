package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorPalettedPermutations;

import java.util.List;
import java.util.Map;

public interface DefaultedAccessorPalettedPermutations extends AccessorPalettedPermutations
{
    @Override
    default List<ResourceLocation> atlasviewer$getTextures() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default Map<String, ResourceLocation> atlasviewer$getPermutations() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default ResourceLocation atlasviewer$getPaletteKey() { throw new UnsupportedOperationException("Not injected"); }
}
