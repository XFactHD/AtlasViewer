package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.textures.GpuTextureView;

public interface IMipAwareTextureAtlas
{
    default GpuTextureView atlasview$getMippedTextureView(int mipLevel)
    {
        throw new UnsupportedOperationException("Not injected");
    }
}
