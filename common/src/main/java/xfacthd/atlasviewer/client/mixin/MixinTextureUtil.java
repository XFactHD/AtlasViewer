package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.TextureUtil;
import org.lwjgl.opengl.ARBClearTexture;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.util.ClientUtils;

import java.nio.ByteBuffer;

@Mixin(TextureUtil.class)
public class MixinTextureUtil
{
    @Inject(
            method = "prepareImage(Lcom/mojang/blaze3d/platform/NativeImage$InternalGlFormat;IIII)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/GlStateManager;_texImage2D(IIIIIIIILjava/nio/IntBuffer;)V",
                    shift = At.Shift.AFTER,
                    remap = false
            )
    )
    private static void atlasviewer$clearImage(NativeImage.InternalGlFormat format, int texId, int mipLevl, int width, int height, CallbackInfo ci, @Local(ordinal = 4) int level)
    {
        if (ClientUtils.isArbClearTextureSupported())
        {
            ARBClearTexture.glClearTexImage(texId, level, format.glFormat(), GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        }
    }
}
