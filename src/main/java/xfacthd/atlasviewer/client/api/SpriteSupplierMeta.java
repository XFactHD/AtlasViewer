package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;

public final class SpriteSupplierMeta
{
    private String sourcePackId = null;
    private Class<?> sourceType = null;
    private SourceAwareness sourceAwareness = SourceAwareness.SPRITESUPPLIER_UNAWARE;

    public void readFromSpriteSourceMeta(SpriteSource source)
    {
        SpriteSourceMeta srcMeta = ((IPackAwareSpriteSource) source).atlasviewer$getMeta();
        sourcePackId = srcMeta.getSourcePack();
        sourceType = source.getClass();
        sourceAwareness = srcMeta.getSourceAwareness();
    }

    public String getSpriteSourceSourcePack()
    {
        return sourcePackId;
    }

    public Class<?> getSpriteSourceType()
    {
        return sourceType;
    }

    public SourceAwareness getSourceAwareness()
    {
        return sourceAwareness;
    }
}
