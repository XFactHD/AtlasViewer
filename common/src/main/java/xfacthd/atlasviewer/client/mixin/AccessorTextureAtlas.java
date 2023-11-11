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
    @Accessor("texturesByName")
    Map<ResourceLocation, TextureAtlasSprite> atlasviewer$getTexturesByName();

    @Accessor("width")
    int atlasviewer$getWidth();

    @Accessor("height")
    int atlasviewer$getHeight();

    @Accessor("mipLevel")
    int atlasviewer$getMipLevel();
}
