package io.github.xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import org.lwjgl.opengl.ARBClearTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import io.github.xfacthd.atlasviewer.client.util.ClientUtils;

import java.nio.ByteBuffer;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice")
public class MixinGlDevice {
    @ModifyReturnValue(
            method = "createTexture(Ljava/lang/String;ILcom/mojang/blaze3d/GpuFormat;IIII)Lcom/mojang/blaze3d/textures/GpuTexture;",
            at = @At("RETURN")
    )
    private static GpuTexture atlasviewer$clearImage(GpuTexture texture) {
        GpuFormat format = texture.getFormat();
        if (!ClientUtils.isArbClearTextureSupported() || !format.hasColorAspect()) {
            return texture;
        }
        if ((texture.usage() & GpuTexture.USAGE_CUBEMAP_COMPATIBLE) != 0) {
            return texture;
        }

        int texId = ((GlTexture) texture).glId();
        int extFormat = GlConst.toGlExternalId(format);
        int type = GlConst.toGlType(format);
        for (int level = 0; level < texture.getMipLevels(); level++) {
            ARBClearTexture.glClearTexImage(texId, level, extFormat, type, (ByteBuffer) null);
        }

        return texture;
    }
}
