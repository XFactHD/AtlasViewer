package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.LazyLoadedImage;
import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Unstitcher.RegionInstance.class)
public interface AccessorUnstitcherRegionInstance
{
    @Accessor("image")
    LazyLoadedImage atlasviewer$getImage();
}
