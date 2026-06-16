package io.github.xfacthd.atlasviewer.client.duck;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.atlasviewer.client.mixin.AccessorSpriteContents;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorSpriteContents extends AccessorSpriteContents {
    @Override
    default int atlasviewer$callGetFrameCount() {
        throw new UnsupportedOperationException("Not injected");
    }

    @Override
    default NativeImage[] atlasviewer$getByMipLevel() {
        throw new UnsupportedOperationException("Not injected");
    }

    @Override
    default SpriteContents.@Nullable AnimatedTexture atlasviewer$getAnimatedTexture() {
        throw new UnsupportedOperationException("Not injected");
    }
}
