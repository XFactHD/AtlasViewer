package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.FrameInfo.class)
public interface AccessorFrameInfo
{
    @Accessor("time")
    int atlasviewer$getTime();
}
