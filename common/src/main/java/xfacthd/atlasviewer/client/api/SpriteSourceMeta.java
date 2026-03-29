package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import org.jspecify.annotations.Nullable;

public sealed class SpriteSourceMeta permits SpriteSourceMeta.Unsupported {
    @Nullable
    private String sourcePackId;
    private SourceAwareness sourceAwareness = SourceAwareness.SPRITESOURCE_UNAWARE;

    /// Capture the pack ID of the resource pack where this sprite source was loaded from
    public void setSourcePack(String packId) {
        sourcePackId = packId;
        sourceAwareness = SourceAwareness.SOURCE_KNOWN;
    }

    /// Indicate that this sprite source was forcefully injected by non-standard means
    /// (i.e. mixin into [SpriteResourceLoader])
    public void setForceInjected() {
        sourceAwareness = SourceAwareness.SPRITESOURCE_FORCED;
    }

    /// Get the pack ID of the resource pack where this sprite source was loaded from
    public @Nullable String getSourcePack() {
        return sourcePackId;
    }

    /// Get the awareness indicating to what degree this sprite source knows about the resource pack it was loaded from
    public SourceAwareness getSourceAwareness() {
        return sourceAwareness;
    }

    public boolean isSourceUnaware() {
        return getSourceAwareness() == SourceAwareness.SPRITESOURCE_UNAWARE;
    }

    public static final class Unsupported extends SpriteSourceMeta {
        public static final Unsupported INSTANCE = new Unsupported();

        private Unsupported() { }

        @Override
        public void setSourcePack(String packId) { }

        @Override
        public void setForceInjected() { }

        @Override
        public SourceAwareness getSourceAwareness() {
            return SourceAwareness.SPRITESOURCE_UNSUPPORTED;
        }
    }
}
