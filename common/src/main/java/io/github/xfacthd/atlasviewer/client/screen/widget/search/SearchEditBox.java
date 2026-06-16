package io.github.xfacthd.atlasviewer.client.screen.widget.search;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SearchEditBox extends EditBox {
    private static final Component TITLE_SEARCH_BAR = Component.translatable("btn.atlasviewer.search");

    public SearchEditBox(int x, int y, int w, int h, @Nullable SearchEditBox prev) {
        super(Minecraft.getInstance().font, x, y, w, h, prev, TITLE_SEARCH_BAR);
        setHint(TITLE_SEARCH_BAR);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == InputConstants.MOUSE_BUTTON_RIGHT && isMouseOver(event.x(), event.y())) {
            setValue("");
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }
}
