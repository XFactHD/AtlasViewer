package xfacthd.atlasviewer.client.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteSources.class)
public interface AccessorSpriteSources
{
    @Accessor("TYPES")
    static BiMap<ResourceLocation, SpriteSourceType> atlasviewer$getTypes()
    {
        throw new AssertionError();
    }
}
