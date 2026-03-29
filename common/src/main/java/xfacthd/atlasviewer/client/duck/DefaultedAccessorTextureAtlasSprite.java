package xfacthd.atlasviewer.client.duck;

import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlasSprite;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorTextureAtlasSprite extends AccessorTextureAtlasSprite {
    @Override
    default int atlasviewer$getPadding() {
        throw new UnsupportedOperationException("Not injected");
    }
}
