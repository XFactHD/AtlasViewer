package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.sources.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import xfacthd.atlasviewer.client.api.IPackAwareSpriteSource;
import xfacthd.atlasviewer.client.api.SpriteSourceMeta;

@Mixin({
        DirectoryLister.class,
        PalettedPermutations.class,
        SingleFile.class,
        Unstitcher.class
})
public class MixinSpriteSourceImpls implements IPackAwareSpriteSource
{
    @Unique
    private final SpriteSourceMeta atlasviewer$meta = new SpriteSourceMeta();

    @Override
    public SpriteSourceMeta atlasviewer$getMeta()
    {
        return atlasviewer$meta;
    }
}
