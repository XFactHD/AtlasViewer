package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.*;
import xfacthd.atlasviewer.client.api.*;

@Mixin(SpriteSource.Output.class)
public interface MixinSpriteSourceOutput
{
    @Shadow
    void add(ResourceLocation pLocation, SpriteSource.SpriteSupplier pSprite);

    /**
     * @author XFactHD
     * @reason Attach SpriteSource source pack ID from Resource to SpriteContents
     */
    @Overwrite
    default void add(ResourceLocation name, Resource resource)
    {
        add(name, loader ->
        {
            SpriteContents contents = loader.loadSprite(name, resource);
            if (contents != null)
            {
                ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$captureMetaFromResource(resource);
            }
            return contents;
        });
    }
}
