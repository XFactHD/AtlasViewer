package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.server.packs.resources.Resource;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorLazyLoadedImage;

public interface DefaultedAccessorLazyLoadedImage extends AccessorLazyLoadedImage
{
    @Override
    default Resource atlasviewer$getResource() { throw new UnsupportedOperationException("Not injected"); }
}
