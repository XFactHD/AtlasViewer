package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.AtlasScreen;
import xfacthd.atlasviewer.client.util.IMipAwareTextureAtlas;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas implements IMipAwareTextureAtlas
{
    @Unique
    private boolean atlasviewer$mipMapEnabled = false;

    @Inject(
            method = "upload",
            at = @At("HEAD")
    )
    private void atlasviewer$onUpload(SpriteLoader.Preparations preps, CallbackInfo ci)
    {
        AtlasScreen.storeAtlasSize(
                (TextureAtlas)(Object) this,
                preps.width(),
                preps.height()
        );
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
}
