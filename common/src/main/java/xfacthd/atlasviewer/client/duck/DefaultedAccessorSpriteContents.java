package xfacthd.atlasviewer.client.duck;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import xfacthd.atlasviewer.client.mixin.AccessorSpriteContents;

public interface DefaultedAccessorSpriteContents extends AccessorSpriteContents
{
    @Override
    default int atlasviewer$callGetFrameCount() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default NativeImage[] atlasviewer$getByMipLevel() { throw new UnsupportedOperationException("Not injected"); }

    @Override
    default SpriteContents.AnimatedTexture atlasviewer$getAnimatedTexture() { throw new UnsupportedOperationException("Not injected"); }
}
