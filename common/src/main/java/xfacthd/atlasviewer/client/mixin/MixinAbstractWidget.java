package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xfacthd.atlasviewer.client.util.IVisibilitySetter;

@Mixin(AbstractWidget.class)
public class MixinAbstractWidget implements IVisibilitySetter
{
    @Shadow public boolean visible;

    @Override
    public void atlasviewer$setVisible(boolean visible)
    {
        this.visible = visible;
    }
}
