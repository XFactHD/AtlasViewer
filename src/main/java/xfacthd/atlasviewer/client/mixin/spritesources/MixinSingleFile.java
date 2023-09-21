package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xfacthd.atlasviewer.client.api.*;

import java.util.Optional;

@Mixin(SingleFile.class)
public abstract class MixinSingleFile implements IPackAwareSpriteSource
{
    @Inject(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteSource$Output;add(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)V"
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    @SuppressWarnings({ "OptionalUsedAsFieldOrParameterType", "OptionalGetWithoutIsPresent" })
    private void atlasviewerresourceAttachSpriteSourceSourcePack(
            ResourceManager manager, SpriteSource.Output output, CallbackInfo ci, ResourceLocation path, Optional<Resource> resource
    )
    {
        ((ISpriteSourcePackAwareResource) resource.get()).atlasviewer$captureMetaFromSpriteSource(
                atlasviewer$getMeta(), getClass(), path
        );
    }
}
