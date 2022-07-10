package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.util.ITextureAtlasSpriteInfoGetter;

@Mixin(TextureAtlasSprite.class)
public class MixinTextureAtlasSprite implements ITextureAtlasSpriteInfoGetter
{
    @Unique
    private TextureAtlasSprite.Info spriteInfo;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void atlasviewer_captureInfo(TextureAtlas pAtlas, TextureAtlasSprite.Info pSpriteInfo, int pMipLevel, int pStorageX, int pStorageY, int pX, int pY, NativeImage pImage, CallbackInfo ci)
    {
        spriteInfo = pSpriteInfo;
    }

    @Override
    public TextureAtlasSprite.Info getInfo() { return spriteInfo; }
}
