package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.textures.GpuTextureView;

public interface IMipAwareTextureAtlas
{
    default void atlasviewer$setMipMapEnabled(boolean enabled) { throw new UnsupportedOperationException("Not injected"); }

    default boolean atlasviewer$isMipMapEnabled() { throw new UnsupportedOperationException("Not injected"); }

    default GpuTextureView atlasview$getMippedTextureView(int mipLevel) { throw new UnsupportedOperationException("Not injected"); }
}
