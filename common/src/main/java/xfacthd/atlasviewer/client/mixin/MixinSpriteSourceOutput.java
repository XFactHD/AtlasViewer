package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import xfacthd.atlasviewer.client.api.*;

@Mixin(SpriteSource.Output.class)
public interface MixinSpriteSourceOutput
{
    @ModifyReturnValue(
            method = "*",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteResourceLoader;loadSprite(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/server/packs/resources/Resource;)Lnet/minecraft/client/renderer/texture/SpriteContents;"
            )
    )
    private static SpriteContents atlasviewer$handleAddResource(SpriteContents contents, ResourceLocation name, Resource resource, SpriteResourceLoader loader)
    {
        if (contents != null)
        {
            ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$captureMetaFromResource(resource);
        }
        return contents;
    }
}
