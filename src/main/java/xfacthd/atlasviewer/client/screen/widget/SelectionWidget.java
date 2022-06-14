package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SelectionWidget<T extends SelectionWidget.SelectionEntry> extends AbstractWidget //TODO: add drag scrolling
{
    private static final ResourceLocation ICONS = new ResourceLocation("minecraft", "textures/gui/resource_packs.png");
    private static final int ENTRY_HEIGHT = 20;
    private final Component title;
    private final Consumer<T> selectCallback;
    private final List<T> entries = new ArrayList<>();
    private T selected = null;
    private boolean extended = false;
    private int scrollOffset = 0;

    public SelectionWidget(int x, int y, int width, Component title, Consumer<T> selectCallback)
    {
        super(x, y, width, ENTRY_HEIGHT, new TextComponent(""));
        this.title = title;
        this.selectCallback = selectCallback;
    }

    @Override
    public void renderButton(PoseStack pstack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(pstack, mouseX, mouseY, partialTicks);

        if (selected != null)
        {
            selected.render(pstack, x, y, width, false, getFGColor(), alpha);
        }
        else
        {
            Font font = Minecraft.getInstance().font;
            drawString(pstack, font, title, x + 6, y + (height - 8) / 2, getFGColor() | Mth.ceil(alpha * 255.0F) << 24);
        }

        if (extended)
        {
            pstack.pushPose();
            pstack.translate(0, 0, 500);

            int boxHeight = Math.max(1, ENTRY_HEIGHT * Math.min(entries.size(), 4)) + 2;

            fill(pstack, x,     y + ENTRY_HEIGHT - 1, x + width,     y + ENTRY_HEIGHT + boxHeight - 1, 0xFFFFFFFF);
            fill(pstack, x + 1, y + ENTRY_HEIGHT,     x + width - 1, y + ENTRY_HEIGHT + boxHeight - 2, 0xFF000000);

            RenderSystem.setShaderTexture(0, ICONS);
            blit(pstack, x + width - 17, y + 6, 114, 5, 11, 7);

            T hoverEntry = getEntryAtPosition(mouseX, mouseY);

            for (int i = 0; i < 4; i++)
            {
                int idx = i + scrollOffset;
                if (idx < entries.size())
                {
                    int entryY = y + ((i + 1) * ENTRY_HEIGHT);

                    T entry = entries.get(idx);
                    entry.render(pstack, x + 1, entryY, width - 2, entry == hoverEntry, getFGColor(), alpha);
                }
            }

            if (entries.size() > 4)
            {
                float scale = 4F / (float)entries.size();
                int scrollY = y + (int)(ENTRY_HEIGHT * scrollOffset * scale) + ENTRY_HEIGHT;
                int barHeight = (int)(ENTRY_HEIGHT * 4 * scale + 1);

                fill(pstack, x + width - 5, scrollY,     x + width - 1, scrollY + barHeight,     0xFF666666);
                fill(pstack, x + width - 4, scrollY + 1, x + width - 2, scrollY + barHeight - 1, 0xFFAAAAAA);
            }

            pstack.popPose();
        }
        else
        {
            RenderSystem.setShaderTexture(0, ICONS);
            blit(pstack, x + width - 17, y + 6, 82, 20, 11, 7);
        }
    }

    @Override
    public int getHeight()
    {
        if (extended)
        {
            return ENTRY_HEIGHT * (Math.min(entries.size(), 4) + 1) + 1;
        }
        return ENTRY_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (active && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + getHeight())
        {
            int maxX = x + width - (entries.size() > 4 ? 5 : 0);
            int maxY = y + ENTRY_HEIGHT * Math.min(entries.size() + 1, 5);
            if (extended && mouseX < maxX && mouseY > (y + ENTRY_HEIGHT) && mouseY < maxY)
            {
                setSelected(getEntryAtPosition(mouseX, mouseY), true);
            }

            if ((mouseY < y + ENTRY_HEIGHT && mouseX < x + width) || mouseX < maxX)
            {
                extended = !extended;
                scrollOffset = 0;
            }

            playDownSound(Minecraft.getInstance().getSoundManager());

            return true;
        }

        extended = false;
        scrollOffset = 0;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        int maxY = y + ENTRY_HEIGHT * Math.min(entries.size() + 1, 5);
        if (extended && mouseX >= x && mouseX <= x + width && mouseY > y + ENTRY_HEIGHT && mouseY < maxY)
        {
            if (delta < 0 && scrollOffset < entries.size() - 4)
            {
                scrollOffset++;
            }
            else if (delta > 0 && scrollOffset > 0)
            {
                scrollOffset--;
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY)
    {
        if (!active || !visible) { return false; }
        return pMouseX >= x && pMouseY >= y && pMouseX < (x + width) && pMouseY < (y + getHeight());
    }

    private T getEntryAtPosition(double mouseX, double mouseY)
    {
        if (mouseX < x || mouseX > x + width || mouseY < (y + ENTRY_HEIGHT) || mouseY > (y + (ENTRY_HEIGHT * 5)))
        {
            return null;
        }

        double posY = mouseY - (y + ENTRY_HEIGHT);
        int idx = (int) (posY / ENTRY_HEIGHT) + scrollOffset;

        return idx < entries.size() ? entries.get(idx) : null;
    }

    public void addEntry(T entry) { entries.add(entry); }

    public void setSelected(T selected, boolean notify)
    {
        this.selected = selected;
        if (notify && selectCallback != null)
        {
            selectCallback.accept(selected);
        }
    }

    public T getSelected() { return selected; }

    public Stream<T> stream() { return entries.stream(); }

    @Override
    public void updateNarration(NarrationElementOutput output) { }

    public static class SelectionEntry implements GuiEventListener
    {
        private final Component message;

        public SelectionEntry(Component message) { this.message = message; }

        public void render(PoseStack pstack, int x, int y, int width, boolean hovered, int fgColor, float alpha)
        {
            if (hovered)
            {
                fill(pstack, x, y, x + width, y + ENTRY_HEIGHT, 0xFFA0A0A0);
            }

            Font font = Minecraft.getInstance().font;
            FormattedCharSequence text = Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(message, width - 12)));
            font.drawShadow(pstack, text, x + 6, y + 6, fgColor | Mth.ceil(alpha * 255.0F) << 24);
        }
    }
}