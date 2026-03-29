package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.*;
import xfacthd.atlasviewer.client.util.WrappedSpriteSource;

@Mixin(SpriteContents.class)
public class MixinSpriteContents implements ISpriteSourcePackAwareSpriteContents {
    @Unique
    private boolean atlasviewer$metaReceived = false;
    @Unique
    @Nullable
    private String atlasviewer$spriteSourceSourcePack;
    @Unique
    @Nullable
    private SpriteSource atlasviewer$spriteSource;
    @Unique
    private SourceAwareness atlasviewer$sourceAwareness = SourceAwareness.SPRITECONTENTS_UNAWARE;
    @Unique
    @Nullable
    private String atlasviewer$textureSourcePack;
    @Unique
    @Nullable
    private Identifier atlasviewer$originalPath = null;

    @Override
    public void atlasviewer$setSpriteSourceSourcePack(
            @Nullable String packId,
            @Nullable SpriteSource spriteSource,
            SourceAwareness awareness,
            @Nullable String texSrcPackId,
            @Nullable Identifier path
    ) {
        // Prevent overwriting metadata already set for these contents
        if (atlasviewer$metaReceived) {
            return;
        }

        atlasviewer$metaReceived = true;
        atlasviewer$spriteSourceSourcePack = packId;
        atlasviewer$spriteSource = WrappedSpriteSource.resolve(spriteSource);
        atlasviewer$sourceAwareness = awareness;
        atlasviewer$textureSourcePack = texSrcPackId;
        atlasviewer$originalPath = path;
    }

    @Override
    public void atlasviewer$captureMetaFromResource(Resource resource) {
        atlasviewer$setSpriteSourceSourcePack(
                resource.atlasviewer$getSpriteSourceSourcePack(),
                resource.atlasviewer$getSpriteSource(),
                resource.atlasviewer$getSourceAwareness(),
                resource.sourcePackId(),
                resource.atlasviewer$getOriginalPath()
        );
    }

    @Override
    public void atlasviewer$captureMetaFromSpriteSupplier(SpriteSource.DiscardableLoader supplier, Resource sourceImage) {
        SpriteSupplierMeta meta = ((ISpriteSourcePackAwareLoader) supplier).atlasviewer$getMeta();
        atlasviewer$setSpriteSourceSourcePack(
                meta.getSpriteSourceSourcePack(),
                meta.getSpriteSource(),
                meta.getSourceAwareness(),
                sourceImage.sourcePackId(),
                sourceImage.atlasviewer$getOriginalPath()
        );
    }

    @Override
    public @Nullable String atlasviewer$getSpriteSourceSourcePack() {
        return atlasviewer$spriteSourceSourcePack;
    }

    @Override
    public @Nullable SpriteSource atlasviewer$getSpriteSource() {
        return atlasviewer$spriteSource;
    }

    @Override
    public SourceAwareness atlasviewer$getSourceAwareness() {
        return atlasviewer$sourceAwareness;
    }

    @Override
    public @Nullable String atlasviewer$getTextureSourcePack() {
        return atlasviewer$textureSourcePack;
    }

    @Override
    public @Nullable Identifier atlasviewer$getOriginalPath() {
        return atlasviewer$originalPath;
    }
}
