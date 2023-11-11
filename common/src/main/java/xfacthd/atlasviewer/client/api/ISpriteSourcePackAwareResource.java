package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;

/**
 * Implemented on {@link net.minecraft.server.packs.resources.Resource} to allow it to keep track of the resource pack
 * where the sprite source loading this resource came from, if this resource is a texture
 */
public interface ISpriteSourcePackAwareResource
{
    /**
     * Capture the pack ID of the pack where the sprite source touching this resource was loaded from,
     * said sprite source and to which degree it is aware of its source pack as well as the original path
     * of this resource
     */
    void atlasviewer$captureMetaFromSpriteSource(SpriteSourceMeta srcMeta, SpriteSource spriteSource, ResourceLocation originalPath);

    /**
     * Get the pack ID of the resource pack where the sprite source touching this resource was loaded from
     */
    String atlasviewer$getSpriteSourceSourcePack();

    /**
     * Get the sprite source that touched this resource
     */
    SpriteSource atlasviewer$getSpriteSource();

    /**
     * Get the awareness indicating to what degree the sprite source touching this resource knows about the
     * resource pack it was loaded from
     */
    SourceAwareness atlasviewer$getSourceAwareness();

    /**
     * Get the original path where this resource is located in the resource pack it was loaded from
     */
    ResourceLocation atlasviewer$getOriginalPath();
}
