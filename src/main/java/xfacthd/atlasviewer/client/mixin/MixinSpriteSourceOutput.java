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
        add(name, () ->
        {
            SpriteContents contents = SpriteLoader.loadSprite(name, resource);
            if (contents != null)
            {
                ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$setSpriteSourceSourcePack(
                        ((ISpriteSourcePackAwareResource) resource).atlasviewer$getSpriteSourceSourcePack(),
                        ((ISpriteSourcePackAwareResource) resource).atlasviewer$getSpriteSourceType(),
                        ((ISpriteSourcePackAwareResource) resource).atlasviewer$getSourceAwareness()
                );
                ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$setTextureSourcePack(resource.sourcePackId());
                ((ISpriteSourcePackAwareSpriteContents) contents).atlasviewer$setOriginalPath(
                        ((ISpriteSourcePackAwareResource) resource).atlasviewer$getOriginalPath()
                );
            }
            return contents;
        });
    }
}
