package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.jspecify.annotations.Nullable;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareLoader;
import xfacthd.atlasviewer.client.api.SpriteSupplierMeta;

public final class WrappedDiscardableLoader implements SpriteSource.DiscardableLoader, ISpriteSourcePackAwareLoader
{
    private final SpriteSource.DiscardableLoader wrapped;
    private final SpriteSupplierMeta meta = new SpriteSupplierMeta();

    private WrappedDiscardableLoader(SpriteSource.DiscardableLoader wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    @Nullable
    public SpriteContents get(SpriteResourceLoader loader)
    {
        return wrapped.get(loader);
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

    public static SpriteSource.DiscardableLoader of(SpriteSource.DiscardableLoader wrapped)
    {
        if (wrapped instanceof ISpriteSourcePackAwareLoader)
        {
            return wrapped;
        }
        return new WrappedDiscardableLoader(wrapped);
    }

    public static SpriteSource.DiscardableLoader resolve(SpriteSource.DiscardableLoader supplier)
    {
        if (supplier instanceof WrappedDiscardableLoader wrapped)
        {
            return wrapped.wrapped;
        }
        return supplier;
    }
}
