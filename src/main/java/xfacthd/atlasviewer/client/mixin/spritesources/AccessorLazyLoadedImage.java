package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LazyLoadedImage.class)
public interface AccessorLazyLoadedImage
{
    @Accessor("resource")
    Resource atlasviewer$getResource();
}
