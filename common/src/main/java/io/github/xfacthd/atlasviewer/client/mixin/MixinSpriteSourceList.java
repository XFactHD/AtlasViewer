package io.github.xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.xfacthd.atlasviewer.client.sourcehandling.SpriteSourceAwareSpriteOutput;
import io.github.xfacthd.atlasviewer.client.sourcehandling.SpriteSourceManager;
import io.github.xfacthd.atlasviewer.client.sourcehandling.WrappedSpriteSource;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import io.github.xfacthd.atlasviewer.AtlasViewer;

import java.util.Collection;
import java.util.List;
import java.util.Set;

// Use higher priority to make reasonably sure that we are injected after weirdos who forcefully inject sprite sources
@Mixin(value = SpriteSourceList.class, priority = 2000)
public class MixinSpriteSourceList {
    @WrapOperation(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource;run(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;)V"
            )
    )
    @Group(name = "SpriteSourceList#list() - source execute lambda", min = 1, max = 1)
    private static void atlasviewer$makeOutputSourceAware(
            SpriteSource source, ResourceManager resMgr, SpriteSource.Output output, Operation<Void> operation
    ) {
        operation.call(source, resMgr, new SpriteSourceAwareSpriteOutput(source, output));
    }

    @WrapOperation(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource;run(Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;Ljava/util/Set;)V"
            )
    )
    @Group(name = "SpriteSourceList#list() - source execute lambda", min = 1, max = 1)
    private static void atlasviewer$makeOutputSourceAwareNeoForge(
            SpriteSource source, ResourceManager resMgr, SpriteSource.Output output, Set<MetadataSectionType<?>> additionalMetadata, Operation<Void> operation
    ) {
        operation.call(source, resMgr, new SpriteSourceAwareSpriteOutput(source, output), additionalMetadata);
    }

    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"
            )
    )
    private static boolean atlasviewer$spriteSourceAttachSourcePack(
            List<SpriteSource> sources, Collection<SpriteSource> sourcesToAdd, Operation<Boolean> operation, @Local Resource resource
    ) {
        String packId = resource.sourcePackId();
        sourcesToAdd = sourcesToAdd.stream()
                .map(src -> {
                    src = WrappedSpriteSource.of(src);
                    src.atlasviewer$getMeta().setSourcePack(packId);
                    return src;
                })
                .toList();
        return operation.call(sources, sourcesToAdd);
    }

    @Inject(
            method = "load",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void atlasviewer$spriteSourceAttachSourcePackVerify(
            ResourceManager pResourceManager,
            Identifier pLocation,
            CallbackInfoReturnable<SpriteResourceLoader> cir,
            Identifier path,
            List<SpriteSource> sources
    ) {
        long count = sources.stream()
                .filter(src -> src.atlasviewer$getMeta().isSourceUnaware())
                .peek(src -> {
                    AtlasViewer.LOGGER.error(
                            "SpriteSource {} did not receive its source pack, the source is most likely injected through non-standard means",
                            SpriteSourceManager.stringifySpriteSource(src)
                    );
                    src.atlasviewer$getMeta().setForceInjected();
                }).count();
        if (count > 0L) {
            AtlasViewer.LOGGER.error(
                    "=== {} SpriteSources for atlas '{}' did not receive their source packs ===",
                    count,
                    pLocation
            );
        }
    }
}
