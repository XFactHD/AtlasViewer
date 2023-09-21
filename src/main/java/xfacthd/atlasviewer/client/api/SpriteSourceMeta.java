package xfacthd.atlasviewer.client.api;

/**
 * Capture the pack ID of the resource pack where this sprite source was loaded from
 */
public sealed class SpriteSourceMeta permits SpriteSourceMeta.Unsupported
{
    private String sourcePackId;
    private SourceAwareness sourceAwareness = SourceAwareness.SPRITESOURCE_UNAWARE;

    /**
     * Capture the pack ID of the resource pack where this sprite source was loaded from
     */
    public void setSourcePack(String packId)
    {
        sourcePackId = packId;
        sourceAwareness = SourceAwareness.SOURCE_KNOWN;
    }

    /**
     * Indicate that this sprite source was forcefully injected by non-standard means
     * (i.e. mixin into {@link net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader})
     */
    public void setForceInjected()
    {
        sourceAwareness = SourceAwareness.SPRITESOURCE_FORCED;
    }

    /**
     * Get the pack ID of the resource pack where this sprite source was loaded from
     */
    public String getSourcePack()
    {
        return sourcePackId;
    }

    /**
     * Get the awareness indicating to what degree this sprite source knows about the resource pack it was loaded from
     */
    public SourceAwareness getSourceAwareness()
    {
        return sourceAwareness;
    }

    public boolean isSourceUnaware()
    {
        return sourceAwareness == SourceAwareness.SPRITESOURCE_UNAWARE;
    }



    public static final class Unsupported extends SpriteSourceMeta
    {
        public static final Unsupported INSTANCE = new Unsupported();

        @Override
        public void setSourcePack(String packId) { }

        @Override
        public void setForceInjected() { }

        @Override
        public SourceAwareness getSourceAwareness()
        {
            return SourceAwareness.SPRITESOURCE_UNSUPPORTED;
        }
    }
}
