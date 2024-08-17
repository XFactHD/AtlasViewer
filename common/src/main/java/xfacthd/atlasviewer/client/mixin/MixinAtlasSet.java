package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(AtlasSet.class)
public class MixinAtlasSet
{
    @Shadow @Final private Map<ResourceLocation, AtlasSet.AtlasEntry> atlases;

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void atlasviewer$setAtlasMipAware(Map<ResourceLocation, ResourceLocation> $$0, TextureManager manager, CallbackInfo ci)
    {
        atlases.values().forEach(atlasEntry -> atlasEntry.atlas().atlasviewer$setMipMapEnabled(true));
    }
}
