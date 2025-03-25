package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.client.util.WrappedSpriteSource;

public final class SpriteSupplierMeta
{
    @Nullable
    private String sourcePackId = null;
    @Nullable
    private SpriteSource spriteSource = null;
    private SourceAwareness sourceAwareness = SourceAwareness.SPRITESUPPLIER_UNAWARE;

    public void readFromSpriteSourceMeta(SpriteSource source)
    {
        SpriteSourceMeta srcMeta = source.atlasviewer$getMeta();
        sourcePackId = srcMeta.getSourcePack();
        spriteSource = WrappedSpriteSource.resolve(source);
        sourceAwareness = srcMeta.getSourceAwareness();
    }

    @Nullable
    public String getSpriteSourceSourcePack()
    {
        return sourcePackId;
    }

    @Nullable
    public SpriteSource getSpriteSource()
    {
        return spriteSource;
    }

    public SourceAwareness getSourceAwareness()
    {
        return sourceAwareness;
    }
}
