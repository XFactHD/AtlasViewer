package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Unstitcher.class)
public interface AccessorUnstitcher
{
    @Accessor("resource")
    ResourceLocation atlasviewer$getResource();

    @Accessor("regions")
    List<Unstitcher.Region> atlasviewer$getRegions();

    @Accessor("xDivisor")
    double atlasviewer$getXDivisor();

    @Accessor("yDivisor")
    double atlasviewer$getYDivisor();
}
