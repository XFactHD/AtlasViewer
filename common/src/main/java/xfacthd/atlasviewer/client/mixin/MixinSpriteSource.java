package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.spongepowered.asm.mixin.Mixin;
import xfacthd.atlasviewer.client.api.IPackAwareSpriteSource;
import xfacthd.atlasviewer.client.api.SpriteSourceMeta;

@Mixin(SpriteSource.class)
public interface MixinSpriteSource extends IPackAwareSpriteSource
{
    @Override
    default SpriteSourceMeta atlasviewer$getMeta()
    {
        return SpriteSourceMeta.Unsupported.INSTANCE;
    }
}
