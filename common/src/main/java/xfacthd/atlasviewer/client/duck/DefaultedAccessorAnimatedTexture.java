package xfacthd.atlasviewer.client.duck;

import net.minecraft.client.renderer.texture.SpriteContents;
import xfacthd.atlasviewer.client.mixin.AccessorAnimatedTexture;

import java.util.List;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorAnimatedTexture extends AccessorAnimatedTexture {
    @Override
    default boolean atlasviewer$getInterpolateFrames() {
        throw new UnsupportedOperationException("Not injected");
    }

    @Override
    default List<SpriteContents.FrameInfo> atlasviewer$getFrames() {
        throw new UnsupportedOperationException("Not injected");
    }
}
