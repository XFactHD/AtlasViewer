package xfacthd.atlasviewer.client.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteResourceLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SpriteSource.Output.class)
public interface MixinSpriteSourceOutput {
    @ModifyReturnValue(
            method = "*",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/renderer/texture/atlas/SpriteResourceLoader;loadSprite(Lnet/minecraft/resources/Identifier;Lnet/minecraft/server/packs/resources/Resource;)Lnet/minecraft/client/renderer/texture/SpriteContents;"
            )
    )
    private static @Nullable SpriteContents atlasviewer$handleAddResource(@Nullable SpriteContents contents, Identifier name, Resource resource, SpriteResourceLoader loader) {
        if (contents != null) {
            contents.atlasviewer$captureMetaFromResource(resource);
        }
        return contents;
    }
}
