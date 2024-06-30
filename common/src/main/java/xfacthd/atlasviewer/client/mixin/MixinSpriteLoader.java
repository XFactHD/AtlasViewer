package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

import java.util.function.Function;

@Mixin(SpriteLoader.class)
public class MixinSpriteLoader
{
    @WrapOperation(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/function/Function;apply(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    private static Object atlasviewer$wrapSpriteSupplierExecution(
            Function<SpriteResourceLoader, SpriteContents> supplier, Object loader, Operation<Object> operation
    )
    {
        Object contents = operation.call(supplier, loader);
        SpriteSourceManager.copySpriteSupplierMetaToSpriteContents(supplier, (SpriteContents) contents);
        return contents;
    }
}
