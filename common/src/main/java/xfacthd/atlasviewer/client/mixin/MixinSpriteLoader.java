package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mixin(SpriteLoader.class)
public class MixinSpriteLoader
{
    @WrapOperation(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Loader;get(Lnet/minecraft/client/renderer/texture/atlas/SpriteResourceLoader;)Lnet/minecraft/client/renderer/texture/SpriteContents;"
            )
    )
    private static SpriteContents atlasviewer$wrapSpriteSupplierExecution(
            SpriteSource.Loader supplier, SpriteResourceLoader loader, Operation<SpriteContents> operation
    )
    {
        SpriteContents contents = operation.call(supplier, loader);
        SpriteSourceManager.copySpriteSupplierMetaToSpriteContents(supplier, contents);
        return contents;
    }
}
