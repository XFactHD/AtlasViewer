package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.AtlasSet;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AtlasSet.class)
public interface AccessorAtlasSet
{
    @Accessor
    Map<ResourceLocation, TextureAtlas> getAtlases();
}
