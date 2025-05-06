package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareSpriteSupplier;

import java.util.function.Predicate;

public record SpriteSourceAwareSpriteOutput(SpriteSource source, SpriteSource.Output wrapped) implements SpriteSource.Output
{
    @Override
    public void add(ResourceLocation name, Resource resource)
    {
        resource.atlasviewer$captureMetaFromSpriteSource(source.atlasviewer$getMeta(), source, name);
        wrapped.add(name, resource);
    }

    @Override
    public void add(ResourceLocation name, SpriteSource.SpriteSupplier supplier)
    {
        supplier = WrappedSpriteSupplier.of(supplier);
        ((ISpriteSourcePackAwareSpriteSupplier) supplier).atlasviewer$getMeta().readFromSpriteSourceMeta(source);
        wrapped.add(name, supplier);
    }

    @Override
    public void removeAll(Predicate<ResourceLocation> predicate)
    {
        wrapped.removeAll(predicate);
    }
}
