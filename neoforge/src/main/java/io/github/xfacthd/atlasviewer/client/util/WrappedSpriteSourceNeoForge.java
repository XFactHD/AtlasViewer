package io.github.xfacthd.atlasviewer.client.util;

import io.github.xfacthd.atlasviewer.client.sourcehandling.WrappedSpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Set;

public final class WrappedSpriteSourceNeoForge extends WrappedSpriteSource {
    public WrappedSpriteSourceNeoForge(SpriteSource wrapped) {
        super(wrapped);
    }

    @Override
    public void run(ResourceManager resourceManager, Output output, Set<MetadataSectionType<?>> additionalMetadata) {
        wrapped.run(resourceManager, output, additionalMetadata);
    }
}
