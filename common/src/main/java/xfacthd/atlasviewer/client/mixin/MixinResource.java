package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.*;

@Mixin(Resource.class)
public class MixinResource implements ISpriteSourcePackAwareResource {
    @Unique
    @Nullable
    private String atlasviewer$spriteSourceSourcePack = null;
    @Unique
    @Nullable
    private SpriteSource atlasviewer$spriteSource = null;
    @Unique
    private SourceAwareness atlasviewer$sourceAwarenes = SourceAwareness.RESOURCE_UNAWARE;
    @Unique
    @Nullable
    private Identifier atlasviewer$originalPath = null;

    @Override
    public void atlasviewer$captureMetaFromSpriteSource(
            SpriteSourceMeta srcMeta, SpriteSource spriteSource, Identifier originalPath
    ) {
        atlasviewer$spriteSourceSourcePack = srcMeta.getSourcePack();
        atlasviewer$spriteSource = spriteSource;
        atlasviewer$sourceAwarenes = srcMeta.getSourceAwareness();
        atlasviewer$originalPath = originalPath;
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
        return atlasviewer$sourceAwarenes;
    }

    @Override
    public @Nullable Identifier atlasviewer$getOriginalPath() {
        return atlasviewer$originalPath;
    }
}
