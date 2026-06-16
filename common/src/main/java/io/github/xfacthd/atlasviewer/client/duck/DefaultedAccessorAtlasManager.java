package io.github.xfacthd.atlasviewer.client.duck;

import net.minecraft.client.resources.model.sprite.AtlasManager;
import net.minecraft.resources.Identifier;
import io.github.xfacthd.atlasviewer.client.mixin.AccessorAtlasManager;

import java.util.Map;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorAtlasManager extends AccessorAtlasManager {
    @Override
    default Map<Identifier, AtlasManager.AtlasEntry> atlasviewer$getAtlasesByTexture() {
        throw new UnsupportedOperationException("Not injected");
    }
}
