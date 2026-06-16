package io.github.xfacthd.atlasviewer.client.screen.widget.search;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;

public final class SearchButton extends Button.Plain {
    private final SearchHandler handler;

    public SearchButton(int x, int y, int width, int height, SearchHandler handler) {
        super(x, y, width, height, Component.empty(), _ -> { }, Button.DEFAULT_NARRATION);
        this.handler = handler;
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractContents(graphics, mouseX, mouseY, partialTick);

        if (active) {
            int count = handler.getResultCount();
            int focusedIdx = handler.getFocusedResultIndex();
            String text = focusedIdx > -1 ? "%d/%d".formatted((focusedIdx + 1), count) : Integer.toString(count);

            graphics.pose().pushMatrix();
            Font font = Minecraft.getInstance().font;
            int length = font.width(text);
            boolean scale = length > (width - 9);
            float x = getX() + width - 3 - (font.width(text) * (scale ? .5F : 1F));
            float y = getY() + 5F + (scale ? 2F : 0F);
            graphics.pose().translate(x, y);
            if (scale) {
                graphics.pose().scale(.5F, .5F);
            }
            graphics.text(font, text, 0, 0, count > 0 ? 0xFFFFFFFF : 0xFF0000);
            graphics.pose().popMatrix();
        }
    }

    @Override
    public boolean isHovered() {
        return active && super.isHovered();
    }

    @Override
    public void onPress(InputWithModifiers input) {
        handler.jumpToNextResult();
    }
}
