package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(PalettedPermutations.class)
public interface AccessorPalettedPermutations
{
    @Accessor("textures")
    List<ResourceLocation> atlasviewer$getTextures();
}
