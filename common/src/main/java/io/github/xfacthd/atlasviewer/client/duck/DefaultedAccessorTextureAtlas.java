package io.github.xfacthd.atlasviewer.client.duck;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import io.github.xfacthd.atlasviewer.client.mixin.AccessorTextureAtlas;

import java.util.Map;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorTextureAtlas extends AccessorTextureAtlas {
    @Override
    default Map<Identifier, TextureAtlasSprite> atlasviewer$getTexturesByName() {
        throw new UnsupportedOperationException("Not injected");
    }

    @Override
    default int atlasviewer$getMipLevel() {
        throw new UnsupportedOperationException("Not injected");
    }
}
