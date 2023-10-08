package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Optional;

@Mixin(SingleFile.class)
public interface AccessorSingleFile
{
    @Accessor("resourceId")
    ResourceLocation atlasviewer$getResourceId();

    @Accessor("spriteId")
    Optional<ResourceLocation> atlasviewer$getSpriteId();
}
