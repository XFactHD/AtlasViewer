package xfacthd.atlasviewer.client.screen.widget.search;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public final class SearchEditBox extends EditBox
{
    private static final Component TITLE_SEARCH_BAR = Component.translatable("btn.atlasviewer.search");

    public SearchEditBox(int x, int y, int w, int h, @Nullable SearchEditBox prev)
    {
        super(Minecraft.getInstance().font, x, y, w, h, prev, TITLE_SEARCH_BAR);
        setHint(TITLE_SEARCH_BAR);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn)
    {
        if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT && clicked(mouseX, mouseY))
        {
            setValue("");
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }
}
