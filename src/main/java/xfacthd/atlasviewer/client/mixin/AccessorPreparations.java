package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureAtlas;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TextureAtlas.Preparations.class)
public interface AccessorPreparations
{
    @Accessor
    int getWidth();

    @Accessor
    int getHeight();
}
