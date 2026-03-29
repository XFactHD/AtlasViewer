package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;

/// Implemented on [net.minecraft.client.renderer.texture.SpriteContents] to allow it to keep track of
/// the resource pack where the sprite source creating these sprite contents came from
public interface ISpriteSourcePackAwareSpriteContents {
    /// Capture the pack ID of the pack where the sprite source creating these sprite contents was loaded from, the
    /// type of said sprite source and to which degree it is aware of its source pack as well as the ID of the resource
    /// pack the texture represented by these sprite contents was loaded from and the source path of said texture
    default void atlasviewer$setSpriteSourceSourcePack(
            @Nullable String packId,
            @Nullable SpriteSource spriteSource,
            SourceAwareness awareness,
            @Nullable String texSrcPackId,
            @Nullable Identifier path
    ) { }

    /// Capture the source information from the given [Resource]
    void atlasviewer$captureMetaFromResource(Resource resource);

    /// Capture the source information from the given [SpriteSource.DiscardableLoader] and the given [Resource]
    /// from which the supplier generated these sprite contents
    void atlasviewer$captureMetaFromSpriteSupplier(SpriteSource.DiscardableLoader supplier, Resource sourceImage);

    /// Get the pack ID of the resource pack where the sprite source creating these sprite contents was loaded from
    @Nullable String atlasviewer$getSpriteSourceSourcePack();

    /// Get the sprite source that created these sprite contents
    @Nullable SpriteSource atlasviewer$getSpriteSource();

    /// Get the awareness indicating to what degree the sprite source creating these sprite contents knows about the
    /// resource pack it was loaded from
    SourceAwareness atlasviewer$getSourceAwareness();

    /// Get the pack ID of the resource pack the texture represented by these sprite contents was loaded from
    /// (may be inaccurate due to compound packs)
    @Nullable String atlasviewer$getTextureSourcePack();

    /// Get the original path where the texture represented by these sprite contents is located
    /// in the resource pack it was loaded from
    @Nullable Identifier atlasviewer$getOriginalPath();
}
