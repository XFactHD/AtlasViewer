package xfacthd.atlasviewer.client.api;

/// Implemented on [net.minecraft.client.renderer.texture.atlas.SpriteSource] to allow it to keep track of
/// the resource pack which contains the atlas config from which this source was loaded
public interface IPackAwareSpriteSource {
    /// {@return the {@link SpriteSourceMeta} holding the source information about this sprite source}
    default SpriteSourceMeta atlasviewer$getMeta() {
        throw new UnsupportedOperationException("Not injected");
    }
}
