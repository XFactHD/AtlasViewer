package xfacthd.atlasviewer.client.api;

import net.minecraft.resources.ResourceLocation;

/**
 * Implemented on {@link net.minecraft.server.packs.resources.Resource} to allow it to keep track of the resource pack
 * where the sprite source loading this resource came from, if this resource is a texture
 */
public interface ISpriteSourcePackAwareResource
{
    /**
     * Capture the pack ID of the pack where the sprite source touching this resource was loaded from, the
     * type of said sprite source and to which degree it is aware of its source pack
     */
    void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness);

    /**
     * Get the pack ID of the resource pack where the sprite source touching this resource was loaded from
     */
    String atlasviewer$getSpriteSourceSourcePack();

    /**
     * Get the type of the sprite source that touched this resource
     */
    Class<?> atlasviewer$getSpriteSourceType();

    /**
     * Get the awareness indicating to what degree the sprite source touching this resource knows about the
     * resource pack it was loaded from
     */
    SourceAwareness atlasviewer$getSourceAwareness();

    /**
     * Capture the original path where this resource is located in the resource pack it was loaded from
     */
    void atlasviewer$setOriginalPath(ResourceLocation path);

    /**
     * Get the original path where this resource is located in the resource pack it was loaded from
     */
    ResourceLocation atlasviewer$getOriginalPath();
}
