package io.github.xfacthd.atlasviewer.client.duck;

import net.minecraft.client.gui.screens.Screen;
import io.github.xfacthd.atlasviewer.client.mixin.AccessorGui;

@SuppressWarnings("unused") // Referenced via interface injection
public interface DefaultedAccessorGui extends AccessorGui {
    @Override
    default void atlasviewer$setScreenDirect(Screen screen) {
        throw new UnsupportedOperationException("Not injected");
    }
}
