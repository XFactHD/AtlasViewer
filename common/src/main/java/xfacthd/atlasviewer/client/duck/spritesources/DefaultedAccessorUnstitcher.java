package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorUnstitcher;

import java.util.List;

public interface DefaultedAccessorUnstitcher extends AccessorUnstitcher
{
    @Override
    default ResourceLocation atlasviewer$getResource() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default List<Unstitcher.Region> atlasviewer$getRegions() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default double atlasviewer$getXDivisor() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default double atlasviewer$getYDivisor() { throw new UnsupportedOperationException("Not injected"); }
}
