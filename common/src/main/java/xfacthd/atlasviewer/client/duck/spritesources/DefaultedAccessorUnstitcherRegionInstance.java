package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorUnstitcherRegionInstance;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorUnstitcherRegionInstance extends AccessorUnstitcherRegionInstance {
    @Override
    default LazyLoadedImage atlasviewer$getImage() {
        throw new UnsupportedOperationException("Not injected");
    }
}
