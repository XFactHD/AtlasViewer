package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareSpriteSupplier;
import xfacthd.atlasviewer.client.api.SpriteSupplierMeta;

public final class WrappedSpriteSupplier implements SpriteSource.SpriteSupplier, ISpriteSourcePackAwareSpriteSupplier
{
    private final SpriteSource.SpriteSupplier wrapped;
    private final SpriteSupplierMeta meta = new SpriteSupplierMeta();

    private WrappedSpriteSupplier(SpriteSource.SpriteSupplier wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public SpriteContents apply(SpriteResourceLoader loader)
    {
        return wrapped.apply(loader);
    }

    @Override
    public void discard()
    {
        wrapped.discard();
    }

    @Override
    public SpriteSupplierMeta atlasviewer$getMeta()
    {
        return meta;
    }



    public static SpriteSource.SpriteSupplier of(SpriteSource.SpriteSupplier wrapped)
    {
        if (wrapped instanceof ISpriteSourcePackAwareSpriteSupplier)
        {
            return wrapped;
        }
        return new WrappedSpriteSupplier(wrapped);
    }

    public static SpriteSource.SpriteSupplier resolve(SpriteSource.SpriteSupplier supplier)
    {
        if (supplier instanceof WrappedSpriteSupplier wrapped)
        {
            return wrapped.wrapped;
        }
        return supplier;
    }
}
