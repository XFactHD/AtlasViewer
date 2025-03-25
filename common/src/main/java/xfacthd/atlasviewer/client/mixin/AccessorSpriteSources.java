package xfacthd.atlasviewer.client.mixin;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteSources.class)
public interface AccessorSpriteSources
{
    @Accessor("ID_MAPPER")
    static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpriteSource>> atlasviewer$getTypes()
    {
        throw new AssertionError();
    }
}
