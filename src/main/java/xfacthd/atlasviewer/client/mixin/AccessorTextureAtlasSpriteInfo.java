package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlasSprite.Info.class)
public interface AccessorTextureAtlasSpriteInfo
{
    @Accessor
    AnimationMetadataSection getMetadata();
}
