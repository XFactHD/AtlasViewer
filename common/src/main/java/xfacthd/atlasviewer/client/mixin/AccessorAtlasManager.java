package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AtlasManager.class)
public interface AccessorAtlasManager
{
    @Accessor("atlasByTexture")
    Map<ResourceLocation, AtlasManager.AtlasEntry> atlasviewer$getAtlasesByTexture();
}
