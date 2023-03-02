package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(TextureAtlasSprite.AnimatedTexture.class)
public interface AccessorAnimatedTexture
{
    @Accessor
    List<TextureAtlasSprite.FrameInfo> getFrames();

    @Accessor
    TextureAtlasSprite.InterpolationData getInterpolationData();
}
