package io.github.xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.server.packs.resources.Resource;
import io.github.xfacthd.atlasviewer.client.mixin.spritesources.AccessorLazyLoadedImage;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorLazyLoadedImage extends AccessorLazyLoadedImage {
    @Override
    default Resource atlasviewer$getResource() {
        throw new UnsupportedOperationException("Not injected");
    }
}
