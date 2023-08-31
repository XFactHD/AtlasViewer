package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TextureAtlas.class)
public interface AccessorTextureAtlas
{
    @Accessor
    Map<ResourceLocation, TextureAtlasSprite> getTexturesByName();

    @Accessor
    int getWidth();

    @Accessor
    int getHeight();

    @Accessor
    int getMipLevel();
}
