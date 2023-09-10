package xfacthd.atlasviewer.client.mixin;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareResource;
import xfacthd.atlasviewer.client.api.SourceAwareness;

@Mixin(Resource.class)
public class MixinResource implements ISpriteSourcePackAwareResource
{
    @Unique
    private String atlasviewer$spriteSourceSourcePack = null;
    @Unique
    private Class<?> atlasviewer$spriteSourceType = null;
    @Unique
    private SourceAwareness atlasviewer$sourceAwarenes = SourceAwareness.RESOURCE_UNAWARE;
    @Unique
    private ResourceLocation atlasviewer$originalPath = null;

    @Override
    public void atlasviewer$setSpriteSourceSourcePack(String packId, Class<?> sourceType, SourceAwareness awareness)
    {
        atlasviewer$spriteSourceSourcePack = packId;
        atlasviewer$spriteSourceType = sourceType;
        atlasviewer$sourceAwarenes = awareness;
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
        return atlasviewer$sourceAwarenes;
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
