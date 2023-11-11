package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.*;

@Mixin(Resource.class)
public class MixinResource implements ISpriteSourcePackAwareResource
{
    @Unique
    private String atlasviewer$spriteSourceSourcePack = null;
    @Unique
    private SpriteSource atlasviewer$spriteSource = null;
    @Unique
    private SourceAwareness atlasviewer$sourceAwarenes = SourceAwareness.RESOURCE_UNAWARE;
    @Unique
    private ResourceLocation atlasviewer$originalPath = null;

    @Override
    public void atlasviewer$captureMetaFromSpriteSource(
            SpriteSourceMeta srcMeta, SpriteSource spriteSource, ResourceLocation originalPath
    )
    {
        atlasviewer$spriteSourceSourcePack = srcMeta.getSourcePack();
        atlasviewer$spriteSource = spriteSource;
        atlasviewer$sourceAwarenes = srcMeta.getSourceAwareness();
        atlasviewer$originalPath = originalPath;
    }

    @Override
    public String atlasviewer$getSpriteSourceSourcePack()
    {
        return atlasviewer$spriteSourceSourcePack;
    }

    @Override
    public SpriteSource atlasviewer$getSpriteSource()
    {
        return atlasviewer$spriteSource;
    }

    @Override
    public SourceAwareness atlasviewer$getSourceAwareness()
    {
        return atlasviewer$sourceAwarenes;
    }

    @Override
    public ResourceLocation atlasviewer$getOriginalPath()
    {
        return atlasviewer$originalPath;
    }
}
