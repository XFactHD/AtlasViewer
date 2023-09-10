package xfacthd.atlasviewer.client.api;

import net.minecraft.resources.ResourceLocation;

/**
 * Implemented on {@link net.minecraft.client.renderer.texture.SpriteContents} to allow it to keep track of
 * the resource pack where the sprite source creating these sprite contents came from
 */
public interface ISpriteSourcePackAwareSpriteContents
{
    /**
     * Capture the pack ID of the pack where the sprite source creating these sprite contents was loaded from, the
     * type of said sprite source and to which degree it is aware of its source pack
     */
    void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness);

    /**
     * Get the pack ID of the resource pack where the sprite source creating these sprite contents was loaded from
     */
    String atlasviewer$getSpriteSourceSourcePack();

    /**
     * Get the type of the sprite source that created these sprite contents
     */
    Class<?> atlasviewer$getSpriteSourceType();

    /**
     * Get the awareness indicating to what degree the sprite source creating these sprite contents knows about the
     * resource pack it was loaded from
     */
    SourceAwareness atlasviewer$getSourceAwareness();

    /**
     * Capture the pack ID of the resource pack the texture represented by these sprite contents was loaded from
     * (may be inaccurate due to compound packs)
     */
    void atlasviewer$setTextureSourcePack(String packId);

    /**
     * Get the pack ID of the resource pack the texture represented by these sprite contents was loaded from
     * (may be inaccurate due to compound packs)
     */
    String atlasviewer$getTextureSourcePack();

    /**
     * Capture the original path where the texture represented by these sprite contents is located
     * in the resource pack it was loaded from
     */
    void atlasviewer$setOriginalPath(ResourceLocation path);

    /**
     * Get the original path where the texture represented by these sprite contents is located
     * in the resource pack it was loaded from
     */
    ResourceLocation atlasviewer$getOriginalPath();
}
