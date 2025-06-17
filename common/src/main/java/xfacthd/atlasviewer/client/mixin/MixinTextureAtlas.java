package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.AtlasScreen;
import xfacthd.atlasviewer.client.util.IMipAwareTextureAtlas;

import java.util.Arrays;
import java.util.Objects;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas implements IMipAwareTextureAtlas
{
    @Unique
    private boolean atlasviewer$mipMapEnabled = false;
    @Unique
    private final @Nullable GpuTextureView[] atlasviewer$mippedTextureViews = new GpuTextureView[5];
    @Shadow
    private int mipLevel;

    @Inject(method = "upload", at = @At("HEAD"))
    private void atlasviewer$onUploadHead(SpriteLoader.Preparations preps, CallbackInfo ci)
    {
        //noinspection DataFlowIssue
        AtlasScreen.storeAtlasSize(
                (TextureAtlas)(Object) this,
                preps.width(),
                preps.height()
        );

        for (int i = 1; i < atlasviewer$mippedTextureViews.length; i++)
        {
            GpuTextureView view = atlasviewer$mippedTextureViews[i];
            if (view != null)
            {
                view.close();
            }
        }
        Arrays.fill(atlasviewer$mippedTextureViews, null);
    }

    @Inject(method = "upload", at = @At("TAIL"))
    private void atlasviewer$onUploadTail(SpriteLoader.Preparations preps, CallbackInfo ci)
    {
        //noinspection DataFlowIssue
        TextureAtlas self = (TextureAtlas)(Object) this;
        atlasviewer$mippedTextureViews[0] = self.getTextureView();
        GpuDevice device = RenderSystem.getDevice();
        for (int i = 1; i <= mipLevel; i++)
        {
            atlasviewer$mippedTextureViews[i] = device.createTextureView(self.getTexture(), i, 1);
        }
    }

    @Override
    public void atlasviewer$setMipMapEnabled(boolean enabled)
    {
        atlasviewer$mipMapEnabled = enabled;
    }

    @Override
    public boolean atlasviewer$isMipMapEnabled()
    {
        return atlasviewer$mipMapEnabled;
    }

    @Override
    public GpuTextureView atlasview$getMippedTextureView(int mipLevel)
    {
        return Objects.requireNonNull(atlasviewer$mippedTextureViews[mipLevel], "Requested view for invalid mip level");
    }
}
