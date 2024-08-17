package xfacthd.atlasviewer.client.duck.spritesources;

import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorSingleFile;

import java.util.Optional;

public interface DefaultedAccessorSingleFile extends AccessorSingleFile
{
    @Override
    default ResourceLocation atlasviewer$getResourceId() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default Optional<ResourceLocation> atlasviewer$getSpriteId() { throw new UnsupportedOperationException("Not injected"); }
}
