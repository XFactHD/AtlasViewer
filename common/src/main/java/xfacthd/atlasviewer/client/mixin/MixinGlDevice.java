package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.ARBClearTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.atlasviewer.client.util.ClientUtils;

import java.nio.ByteBuffer;

@Mixin(GlDevice.class)
public class MixinGlDevice
{
    @Inject(
            method = "createTexture(Ljava/lang/String;Lcom/mojang/blaze3d/textures/TextureFormat;III)Lcom/mojang/blaze3d/textures/GpuTexture;",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/opengl/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private static void atlasviewer$clearImage(
            @Nullable String name,
            TextureFormat format,
            int width,
            int height,
            int mipLevels,
            CallbackInfoReturnable<GpuTexture> cir,
            @Local(ordinal = 3) int texId,
            @Local(ordinal = 4) int level
    )
    {
        if (ClientUtils.isArbClearTextureSupported() && format.hasColorAspect())
        {
            ARBClearTexture.glClearTexImage(texId, level, GlConst.toGlExternalId(format), GlConst.toGlType(format), (ByteBuffer) null);
        }
    }
}
