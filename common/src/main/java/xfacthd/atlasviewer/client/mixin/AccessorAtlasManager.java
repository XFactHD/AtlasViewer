package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(AtlasManager.class)
public interface AccessorAtlasManager
{
    @Accessor("atlasByTexture")
    Map<Identifier, AtlasManager.AtlasEntry> atlasviewer$getAtlasesByTexture();
}
