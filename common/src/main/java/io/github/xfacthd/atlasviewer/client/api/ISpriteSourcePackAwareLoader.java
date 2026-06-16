package io.github.xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;

/// Implemented on select [SpriteSource.DiscardableLoader] to allow them to
/// keep track of the resource pack where the sprite source creating this sprite supplier came from.
///
/// This is intended to only be used by individual [SpriteSource]
/// implementations which create custom sprite suppliers
public interface ISpriteSourcePackAwareLoader {
    /// {@return the {@link SpriteSupplierMeta} holding the source information about the {@link SpriteSource}
    ///  that created this sprite supplier}
    SpriteSupplierMeta atlasviewer$getMeta();
}
