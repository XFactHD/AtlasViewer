package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DirectoryLister.class)
public interface AccessorDirectoryLister
{
    @Accessor("sourcePath")
    String atlasviewer$getSourcePath();
}
