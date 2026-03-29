package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TextureAtlas.class)
public interface AccessorTextureAtlas {
    @Accessor("texturesByName")
    Map<Identifier, TextureAtlasSprite> atlasviewer$getTexturesByName();

    @Accessor("maxMipLevel")
    int atlasviewer$getMipLevel();
}
