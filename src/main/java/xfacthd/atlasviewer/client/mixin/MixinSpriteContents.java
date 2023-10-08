package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.*;

@Mixin(SpriteContents.class)
public class MixinSpriteContents implements ISpriteSourcePackAwareSpriteContents
{
    @Unique
    private String atlasviewer$spriteSourceSourcePack;
    @Unique
    private SpriteSource atlasviewer$spriteSource;
    @Unique
    private Class<?> atlasviewer$spriteSourceType;
    @Unique
    private SourceAwareness atlasviewer$sourceAwareness = SourceAwareness.SPRITECONTENTS_UNAWARE;
    @Unique
    private String atlasviewer$textureSourcePack;
    @Unique
    private ResourceLocation atlasviewer$originalPath = null;

    @Override
    @SuppressWarnings("removal")
    public void atlasviewer$setSpriteSourceSourcePack(
            String packId, Class<?> sourceType, SourceAwareness awareness, String texSrcPackId, ResourceLocation path
    )
    {
        atlasviewer$spriteSourceSourcePack = packId;
        atlasviewer$spriteSource = null;
        atlasviewer$spriteSourceType = sourceType;
        atlasviewer$sourceAwareness = awareness;
        atlasviewer$textureSourcePack = texSrcPackId;
        atlasviewer$originalPath = path;
    }

    @Override
    public void atlasviewer$setSpriteSourceSourcePack(
            String packId, SpriteSource spriteSource, SourceAwareness awareness, String texSrcPackId, ResourceLocation path
    )
    {
        atlasviewer$spriteSourceSourcePack = packId;
        atlasviewer$spriteSource = spriteSource;
        atlasviewer$spriteSourceType = spriteSource.getClass();
        atlasviewer$sourceAwareness = awareness;
        atlasviewer$textureSourcePack = texSrcPackId;
        atlasviewer$originalPath = path;
    }

    @Override
    public void atlasviewer$captureMetaFromResource(Resource resource)
    {
        ISpriteSourcePackAwareResource awareResource = (ISpriteSourcePackAwareResource) resource;
        atlasviewer$setSpriteSourceSourcePack(
                awareResource.atlasviewer$getSpriteSourceSourcePack(),
                awareResource.atlasviewer$getSpriteSource(),
                awareResource.atlasviewer$getSourceAwareness(),
                resource.sourcePackId(),
                awareResource.atlasviewer$getOriginalPath()
        );
    }

    @Override
    public void atlasviewer$captureMetaFromSpriteSupplier(SpriteSource.SpriteSupplier supplier, Resource sourceImage)
    {
        SpriteSupplierMeta meta = ((ISpriteSourcePackAwareSpriteSupplier) supplier).atlasviewer$getMeta();
        atlasviewer$setSpriteSourceSourcePack(
                meta.getSpriteSourceSourcePack(),
                meta.getSpriteSource(),
                meta.getSourceAwareness(),
                sourceImage.sourcePackId(),
                ((ISpriteSourcePackAwareResource) sourceImage).atlasviewer$getOriginalPath()
        );
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
    @SuppressWarnings("removal")
    public Class<?> atlasviewer$getSpriteSourceType()
    {
        return atlasviewer$spriteSourceType;
    }

    @Override
    public SourceAwareness atlasviewer$getSourceAwareness()
    {
        return atlasviewer$sourceAwareness;
    }

    @Override
    public String atlasviewer$getTextureSourcePack()
    {
        return atlasviewer$textureSourcePack;
    }

    @Override
    public ResourceLocation atlasviewer$getOriginalPath()
    {
        return atlasviewer$originalPath;
    }
}
