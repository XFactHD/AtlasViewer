package xfacthd.atlasviewer.client.duck;

import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import xfacthd.atlasviewer.client.mixin.AccessorAtlasManager;

import java.util.Map;

public interface DefaultedAccessorAtlasManager extends AccessorAtlasManager
{
    @Override
    default Map<Identifier, AtlasManager.AtlasEntry> atlasviewer$getAtlasesByTexture() { throw new UnsupportedOperationException("Not injected"); }
}
