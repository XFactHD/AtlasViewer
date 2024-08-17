package xfacthd.atlasviewer.client.duck;

import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.AccessorTextureManager;

import java.util.Map;

public interface DefaultedAccessorTextureManager extends AccessorTextureManager
{
    @Override
    default Map<ResourceLocation, AbstractTexture> atlasviewer$getByPath() { throw new UnsupportedOperationException("Not injected"); }
}
