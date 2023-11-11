package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import org.spongepowered.asm.mixin.Mixin;
import xfacthd.atlasviewer.client.api.IPackAwareSpriteSource;

@Mixin(SpriteSource.class)
public interface MixinSpriteSource extends IPackAwareSpriteSource
{

}
