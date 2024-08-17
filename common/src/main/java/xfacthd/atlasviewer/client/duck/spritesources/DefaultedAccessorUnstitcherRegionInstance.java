package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorUnstitcherRegionInstance;

public interface DefaultedAccessorUnstitcherRegionInstance extends AccessorUnstitcherRegionInstance
{
    @Override
    default LazyLoadedImage atlasviewer$getImage() { throw new UnsupportedOperationException("Not injected"); }
}
