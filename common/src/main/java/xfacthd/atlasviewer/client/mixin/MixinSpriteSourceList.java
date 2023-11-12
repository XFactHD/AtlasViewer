package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.texture.atlas.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.api.*;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

import java.util.Collection;
import java.util.List;

// Use higher priority to make reasonably sure that we are injected after weirdos who forcefully inject sprite sources
@Mixin(value = SpriteSourceList.class, priority = 2000)
public class MixinSpriteSourceList
{
    @WrapOperation(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"
            )
    )
    private static boolean atlasviewer$spriteSourceAttachSourcePack(
            List<SpriteSource> sources, Collection<SpriteSource> sourcesToAdd, Operation<Boolean> operation, @Local Resource resource
    )
    {
        String packId = resource.sourcePackId();
        sourcesToAdd.forEach(src -> ((IPackAwareSpriteSource) src).atlasviewer$getMeta().setSourcePack(packId));
        return operation.call(sources, sourcesToAdd);
    }

    @Inject(
            method = "load",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void atlasviewer$spriteSourceAttachSourcePackVerify(
            ResourceManager pResourceManager,
            ResourceLocation pLocation,
            CallbackInfoReturnable<SpriteResourceLoader> cir,
            ResourceLocation path,
            List<SpriteSource> sources
    )
    {
        long count = sources.stream()
                .filter(src -> ((IPackAwareSpriteSource) src).atlasviewer$getMeta().isSourceUnaware())
                .peek(src ->
                {
                    AtlasViewer.LOGGER.error(
                            "SpriteSource {} did not receive its source pack, the source is most likely injected through non-standard means",
                            SpriteSourceManager.stringifySpriteSource(src)
                    );
                    ((IPackAwareSpriteSource) src).atlasviewer$getMeta().setForceInjected();
                }).count();
        if (count > 0L)
        {
            AtlasViewer.LOGGER.error(
                    "=== {} SpriteSources for atlas '{}' did not receive their source packs ===",
                    count,
                    pLocation
            );
        }
    }
}
