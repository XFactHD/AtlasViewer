package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.AtlasScreen;
import xfacthd.atlasviewer.client.util.IMipAwareTextureAtlas;

import java.util.Objects;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas extends AbstractTexture implements IMipAwareTextureAtlas {
    @Shadow
    private GpuTextureView[] mipViews;

    @Inject(method = "upload", at = @At("HEAD"))
    private void atlasviewer$onUploadHead(SpriteLoader.Preparations preps, CallbackInfo ci) {
        //noinspection DataFlowIssue
        AtlasScreen.storeAtlasSize(
                (TextureAtlas) (Object) this,
                preps.width(),
                preps.height()
        );
    }

    @Override
    public GpuTextureView atlasview$getMippedTextureView(int mipLevel) {
        return Objects.requireNonNull(mipViews[mipLevel], "Requested view for invalid mip level");
    }
}
