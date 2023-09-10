package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.atlasviewer.client.api.*;

@Mixin(targets = "net/minecraft/client/renderer/texture/atlas/sources/PalettedPermutations$PalettedSpriteSupplier")
public class MixinPalettedPermutationsPalettedSpriteSupplier implements ISpriteSourcePackAwareSpriteSupplier
{
    @Shadow @Final private LazyLoadedImage baseImage;
    @Unique
    private String atlasviewer$sourcePackId = null;
    @Unique
    private Class<?> atlasviewer$sourceType = null;
    @Unique
    private SourceAwareness atlasviewer$sourceAwareness = SourceAwareness.SPRITESUPPLIER_UNAWARE;

    @Override
    public void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness)
    {
        atlasviewer$sourcePackId = packId;
        atlasviewer$sourceType = sourceType;
        atlasviewer$sourceAwareness = awareness;
    }

    @Inject(
            method = "get()Lnet/minecraft/client/renderer/texture/SpriteContents;",
            at = @At("RETURN")
    )
    private void atlasviewer$contentsAttachSpriteSourceSourcePack(CallbackInfoReturnable<SpriteContents> cir)
    {
        SpriteContents retVal = cir.getReturnValue();
        if (retVal != null)
        {
            ((ISpriteSourcePackAwareSpriteContents) retVal).atlasviewer$setSpriteSourceSourcePack(
                    atlasviewer$sourcePackId, atlasviewer$sourceType, atlasviewer$sourceAwareness
            );
            Resource resource = ((AccessorLazyLoadedImage) baseImage).atlasviewer$getResource();
            ((ISpriteSourcePackAwareSpriteContents) retVal).atlasviewer$setTextureSourcePack(
                    resource.sourcePackId()
            );
            ((ISpriteSourcePackAwareSpriteContents) retVal).atlasviewer$setOriginalPath(
                    ((ISpriteSourcePackAwareResource) resource).atlasviewer$getOriginalPath()
            );
        }
    }
}
