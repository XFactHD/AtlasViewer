package xfacthd.atlasviewer.client.mixin.spritesources;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.*;

@Mixin(value = DirectoryLister.class, priority = 2000)
public class MixinDirectoryListerOculus implements IPackAwareSpriteSource
{
    // Abuse the mixin to inject a "fake" handler method and manually inject the call to it in postApply(),
    // reason being that mixin cannot target the lambda added by Oculus' Overwrite
    @Unique
    @SuppressWarnings("unused")
    private void atlasviewer$resourceAttachSpriteSourceSourcePack(ResourceLocation name, Resource resource)
    {
        ((ISpriteSourcePackAwareResource) resource).atlasviewer$captureMetaFromSpriteSource(
                atlasviewer$getMeta(), getClass(), name
        );
    }
}
