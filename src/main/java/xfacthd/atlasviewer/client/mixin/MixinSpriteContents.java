package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareSpriteContents;
import xfacthd.atlasviewer.client.api.SourceAwareness;

@Mixin(SpriteContents.class)
public class MixinSpriteContents implements ISpriteSourcePackAwareSpriteContents
{
    @Unique
    private String atlasviewer$spriteSourceSourcePack;
    @Unique
    private Class<?> atlasviewer$spriteSourceType;
    @Unique
    private SourceAwareness atlasviewer$sourceAwareness = SourceAwareness.SPRITECONTENTS_UNAWARE;
    @Unique
    private String atlasviewer$textureSourcePack;
    @Unique
    private ResourceLocation atlasviewer$originalPath = null;

    @Override
    public void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness)
    {
        atlasviewer$spriteSourceSourcePack = packId;
        atlasviewer$spriteSourceType = sourceType;
        atlasviewer$sourceAwareness = awareness;
    }

    @Override
    public String atlasviewer$getSpriteSourceSourcePack()
    {
        return atlasviewer$spriteSourceSourcePack;
    }

    @Override
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
    public void atlasviewer$setTextureSourcePack(String packId)
    {
        atlasviewer$textureSourcePack = packId;
    }

    @Override
    public String atlasviewer$getTextureSourcePack()
    {
        return atlasviewer$textureSourcePack;
    }

    @Override
    public void atlasviewer$setOriginalPath(ResourceLocation path)
    {
        atlasviewer$originalPath = path;
    }

    @Override
    public ResourceLocation atlasviewer$getOriginalPath()
    {
        return atlasviewer$originalPath;
    }
}
