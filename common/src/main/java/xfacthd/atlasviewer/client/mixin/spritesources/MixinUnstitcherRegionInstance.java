package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.atlasviewer.client.api.*;

@Mixin(targets = "net/minecraft/client/renderer/texture/atlas/sources/Unstitcher$RegionInstance")
public class MixinUnstitcherRegionInstance implements ISpriteSourcePackAwareSpriteSupplier
{
    @Shadow
    @Final
    private LazyLoadedImage image;
    @Unique
    private final SpriteSupplierMeta atlasviewer$meta = new SpriteSupplierMeta();

    @Override
    public SpriteSupplierMeta atlasviewer$getMeta()
    {
        return atlasviewer$meta;
    }

    @Inject(
            method = "apply(Lnet/minecraft/client/renderer/texture/atlas/SpriteResourceLoader;)Lnet/minecraft/client/renderer/texture/SpriteContents;",
            at = @At(value = "RETURN", ordinal = 0)
    )
    private void atlasviewer$contentsAttachSpriteSourceSourcePack(
            SpriteResourceLoader loader, CallbackInfoReturnable<SpriteContents> cir
    )
    {
        ((ISpriteSourcePackAwareSpriteContents) cir.getReturnValue()).atlasviewer$captureMetaFromSpriteSupplier(
                (SpriteSource.SpriteSupplier) this, ((AccessorLazyLoadedImage) image).atlasviewer$getResource()
        );
    }
}
