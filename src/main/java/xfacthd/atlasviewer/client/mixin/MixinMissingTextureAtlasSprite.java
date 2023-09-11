package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareSpriteContents;
import xfacthd.atlasviewer.client.api.SourceAwareness;

@Mixin(MissingTextureAtlasSprite.class)
public class MixinMissingTextureAtlasSprite
{
    @Inject(
            method = "create",
            at = @At("RETURN")
    )
    private static void atlasviewer$setMissingSpriteSourcePack(CallbackInfoReturnable<SpriteContents> cir)
    {
        SpriteContents contents = cir.getReturnValue();
        ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$setSpriteSourceSourcePack(
                "builtin (synthetic)",
                MissingTextureAtlasSprite.class,
                SourceAwareness.SOURCE_KNOWN
        );
        ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$setTextureSourcePack("builtin (synthetic)");
    }
}