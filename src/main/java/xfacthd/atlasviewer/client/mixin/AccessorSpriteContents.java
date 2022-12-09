package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SpriteContents.class)
public interface AccessorSpriteContents
{
    @Invoker
    int callGetFrameCount();

    @Accessor
    SpriteContents.AnimatedTexture getAnimatedTexture();
}
