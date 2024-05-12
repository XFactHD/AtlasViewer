package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.TextureAtlasHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlasHolder.class)
public interface AccessorTextureAtlasHolder
{
    @Accessor("textureAtlas")
    TextureAtlas atlasviewer$getAtlas();
}
