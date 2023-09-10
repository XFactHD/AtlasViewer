package xfacthd.atlasviewer.client.api;

/**
 * Implemented on select {@link net.minecraft.client.renderer.texture.atlas.SpriteSource.SpriteSupplier} to allow them to
 * keep track of the resource pack where the sprite source creating this sprite supplier came from.
 * <p>
 * This is intended to only be used by individual {@link net.minecraft.client.renderer.texture.atlas.SpriteSource}
 * implementations which create custom sprite suppliers
 */
public interface ISpriteSourcePackAwareSpriteSupplier
{
    /**
     * Capture the pack ID of the pack where the sprite source creating this sprite supplier was loaded from, the
     * type of said sprite source and to which degree it is aware of its source pack
     * <p>
     * The captured data is only to be used by this sprite supplier to copy it to the
     * {@link net.minecraft.client.renderer.texture.SpriteContents} it creates
     */
    void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness);
}
