package xfacthd.atlasviewer.client.mixin;

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

import java.util.Iterator;
import java.util.List;

// Use higher priority to make reasonably sure that we are injected after weirdos who forcefully inject sprite sources
@Mixin(value = SpriteSourceList.class, priority = 2000)
public class MixinSpriteSourceList
{
    @Unique
    private static final ThreadLocal<Integer> ATLASVIEWER$PRE_ADD_LIST_SIZE = ThreadLocal.withInitial(() -> 0);

    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void atlasviewer$spriteSourceAttachSourcePackCapturePreCount(
            ResourceManager pResourceManager,
            ResourceLocation pLocation,
            CallbackInfoReturnable<SpriteResourceLoader> cir,
            ResourceLocation path,
            List<SpriteSource> sources
    )
    {
        ATLASVIEWER$PRE_ADD_LIST_SIZE.set(sources.size());
    }

    @Inject(
            method = "load",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;addAll(Ljava/util/Collection;)Z",
                    shift = At.Shift.AFTER
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private static void atlasviewer$spriteSourceAttachSourcePackApply(
            ResourceManager pResourceManager,
            ResourceLocation pLocation,
            CallbackInfoReturnable<SpriteResourceLoader> cir,
            ResourceLocation path,
            List<SpriteSource> sources,
            Iterator<Resource> resourceIterator,
            Resource resource
    )
    {
        String packId = resource.sourcePackId();
        for (int i = ATLASVIEWER$PRE_ADD_LIST_SIZE.get(); i < sources.size(); i++)
        {
            SpriteSource source = sources.get(i);
            ((IPackAwareSpriteSource) source).atlasviewer$getMeta().setSourcePack(packId);
        }
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
