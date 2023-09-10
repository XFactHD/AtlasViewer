package xfacthd.atlasviewer.client.api;

/**
 * Implemented on {@link net.minecraft.client.renderer.texture.atlas.SpriteSource} to allow it to keep track of
 * the resource pack which contains the atlas config that
 */
public interface IPackAwareSpriteSource
{
    /**
     * Capture the pack ID of the resource pack where this sprite source was loaded from
     */
    default void atlasviewer$setSourcePack(String packId) { }

    /**
     * Indicate that this sprite source was forcefully injected by non-standard means
     * (i.e. mixin into {@link net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader})
     */
    default void atlasviewer$setForceInjected() { }

    /**
     * Get the pack ID of the resource pack where this sprite source was loaded from
     */
    default String atlasviewer$getSourcePack()
    {
        return null;
    }

    /**
     * Get the awareness indicating to what degree this sprite source knows about the resource pack it was loaded from
     */
    default SourceAwareness atlasviewer$getSourceAwareness()
    {
        return SourceAwareness.SPRITESOURCE_UNSUPPORTED;
    }
}
