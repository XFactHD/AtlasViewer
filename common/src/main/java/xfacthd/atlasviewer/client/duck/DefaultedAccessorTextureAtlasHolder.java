package xfacthd.atlasviewer.client.duck;

import net.minecraft.client.renderer.texture.TextureAtlas;
import xfacthd.atlasviewer.client.mixin.AccessorTextureAtlasHolder;

public interface DefaultedAccessorTextureAtlasHolder extends AccessorTextureAtlasHolder
{
    @Override
    default TextureAtlas atlasviewer$getAtlas() { throw new UnsupportedOperationException("Not injected"); }
}
