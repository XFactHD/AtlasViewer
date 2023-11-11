package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.AtlasScreen;

@Mixin(TextureAtlas.class)
public class MixinTextureAtlas
{
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
}
