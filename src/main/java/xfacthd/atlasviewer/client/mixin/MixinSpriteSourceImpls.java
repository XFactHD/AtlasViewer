package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.sources.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.IPackAwareSpriteSource;
import xfacthd.atlasviewer.client.api.SourceAwareness;

@Mixin({
        DirectoryLister.class,
        PalettedPermutations.class,
        SingleFile.class,
        Unstitcher.class
})
public class MixinSpriteSourceImpls implements IPackAwareSpriteSource
{
    @Unique
    private String atlasViewer$sourcePackId = null;
    @Unique
    private SourceAwareness atlasviewer$sourceAwareness = SourceAwareness.SPRITESOURCE_UNAWARE;

    @Override
    public void atlasviewer$setSourcePack(String packId)
    {
        atlasViewer$sourcePackId = packId;
        atlasviewer$sourceAwareness = SourceAwareness.SOURCE_KNOWN;
    }

    @Override
    public void atlasviewer$setForceInjected()
    {
        atlasviewer$sourceAwareness = SourceAwareness.SPRITESOURCE_FORCED;
    }

    @Override
    public String atlasviewer$getSourcePack()
    {
        return atlasViewer$sourcePackId;
    }

    @Override
    public SourceAwareness atlasviewer$getSourceAwareness()
    {
        return atlasviewer$sourceAwareness;
    }
}
