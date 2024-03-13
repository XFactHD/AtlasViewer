package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.api.*;

@Mixin(value = DirectoryLister.class, priority = 2000)
public abstract class MixinDirectoryLister implements IPackAwareSpriteSource
{
    @Inject(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)V"
            )
    )
    private void atlasviewer$resourceAttachSpriteSourceSourcePack(
            FileToIdConverter converter,
            SpriteSource.Output output,
            ResourceLocation name,
            Resource resource,
            CallbackInfo ci
    )
    {
        ((ISpriteSourcePackAwareResource) resource).atlasviewer$captureMetaFromSpriteSource(
                atlasviewer$getMeta(), (SpriteSource) this, name
        );
    }
}
