package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(SpriteContents.AnimatedTexture.class)
public interface AccessorAnimatedTexture
{
    @Accessor
    boolean getInterpolateFrames();

    @Accessor
    List<SpriteContents.FrameInfo> getFrames();
}