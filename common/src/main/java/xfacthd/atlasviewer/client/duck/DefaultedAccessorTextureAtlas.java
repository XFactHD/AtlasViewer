package xfacthd.atlasviewer.client.duck;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlas;

import java.util.Map;

public interface DefaultedAccessorTextureAtlas extends AccessorTextureAtlas
{
    @Override
    default Map<ResourceLocation, TextureAtlasSprite> atlasviewer$getTexturesByName() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default int atlasviewer$getWidth() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default int atlasviewer$getHeight() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default int atlasviewer$getMipLevel() { throw new UnsupportedOperationException("Not injected"); }
}
