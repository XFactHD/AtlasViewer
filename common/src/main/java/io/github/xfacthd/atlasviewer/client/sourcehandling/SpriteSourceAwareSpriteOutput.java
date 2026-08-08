package io.github.xfacthd.atlasviewer.client.sourcehandling;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import io.github.xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareLoader;

import java.util.function.Predicate;

public record SpriteSourceAwareSpriteOutput(SpriteSource source, SpriteSource.Output wrapped) implements SpriteSource.Output {
    @Override
    public void add(Identifier name, Resource resource) {
        resource.atlasviewer$captureMetaFromSpriteSource(source.atlasviewer$getMeta(), source, name);
        wrapped.add(name, resource);
    }

    @Override
    public void add(Identifier name, SpriteSource.DiscardableLoader supplier) {
        supplier = WrappedDiscardableLoader.of(supplier);
        ((ISpriteSourcePackAwareLoader) supplier).atlasviewer$getMeta().readFromSpriteSourceMeta(source);
        wrapped.add(name, supplier);
    }

    @Override
    public void removeAll(Predicate<Identifier> predicate) {
        wrapped.removeAll(predicate);
    }
}
