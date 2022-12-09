package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.AnimatedTexture.class)
public interface AccessorAnimatedTexture
{
    @Accessor
    boolean getInterpolateFrames();
}
