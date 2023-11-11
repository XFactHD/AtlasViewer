package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteContents.class)
public interface AccessorSpriteContents
{
    @Invoker("getFrameCount")
    int atlasviewer$callGetFrameCount();

    @Accessor("byMipLevel")
    NativeImage[] atlasviewer$getByMipLevel();

    @Accessor("animatedTexture")
    SpriteContents.AnimatedTexture atlasviewer$getAnimatedTexture();
}
