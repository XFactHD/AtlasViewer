package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xfacthd.atlasviewer.client.api.*;

@Mixin(Unstitcher.class)
public abstract class MixinUnstitcher implements IPackAwareSpriteSource
{
    @ModifyArg(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$SpriteSupplier;)V"
            )
    )
    private SpriteSource.SpriteSupplier atlasviewer$spriteSupplierAttachSpriteSourceSourcePack(SpriteSource.SpriteSupplier supplier)
    {
        ((ISpriteSourcePackAwareSpriteSupplier) supplier).atlasviewer$getMeta()
                .readFromSpriteSourceMeta((SpriteSource) this);
        return supplier;
    }
}
