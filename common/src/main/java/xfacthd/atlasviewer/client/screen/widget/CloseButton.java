package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class CloseButton extends Button.Plain {
    private static final Component TITLE = Component.literal("x");

    public CloseButton(int x, int y, Screen owner) {
        super(x, y, 12, 12, Component.empty(), _ -> owner.onClose(), Button.DEFAULT_NARRATION);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);

        Font font = Minecraft.getInstance().font;
        graphics.centeredText(font, TITLE, getX() + width / 2, getY() + (height - 10) / 2, 0xFFFFFFFF);
    }
}
