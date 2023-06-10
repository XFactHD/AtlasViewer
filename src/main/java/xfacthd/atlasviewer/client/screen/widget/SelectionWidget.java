package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.*;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

//TODO: add drag scrolling
public class SelectionWidget<T extends SelectionWidget.SelectionEntry<T>> extends AbstractButton
{
    private static final ResourceLocation ICONS = new ResourceLocation("minecraft", "textures/gui/resource_packs.png");
    private static final int ENTRY_HEIGHT = 20;
    private final Screen owner;
    private final Component title;
    private final Consumer<T> selectCallback;
    private final List<T> entries = new ArrayList<>();
    private T focused = null;
    private T selected = null;
    private boolean extended = false;
    private int scrollOffset = 0;

    public SelectionWidget(Screen owner, int x, int y, int width, Component title, Consumer<T> selectCallback)
    {
        super(x, y, width, ENTRY_HEIGHT, Component.empty());
        this.owner = owner;
        this.title = title;
        this.selectCallback = selectCallback;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTicks);

        if (selected != null)
        {
            boolean entryFocused = selected.isFocused();
            selected.focused = false;
            selected.render(graphics, getX(), getY(), width, false, getFGColor(), alpha);
            selected.focused = entryFocused;
        }
        else
        {
            Font font = Minecraft.getInstance().font;
            graphics.drawString(font, title, getX() + 6, getY() + (height - 8) / 2, getFGColor() | Mth.ceil(alpha * 255.0F) << 24);
        }

        if (extended)
        {
            graphics.pose().pushPose();
            graphics.pose().translate(0, 0, 500);

            int boxHeight = Math.max(1, ENTRY_HEIGHT * Math.min(entries.size(), 4)) + 2;

            graphics.fill(getX(),     getY() + ENTRY_HEIGHT - 1, getX() + width,     getY() + ENTRY_HEIGHT + boxHeight - 1, 0xFFFFFFFF);
            graphics.fill(getX() + 1, getY() + ENTRY_HEIGHT,     getX() + width - 1, getY() + ENTRY_HEIGHT + boxHeight - 2, 0xFF000000);

            graphics.blit(ICONS, getX() + width - 17, getY() + 6, 114, 5, 11, 7);

            T hoverEntry = getEntryAtPosition(mouseX, mouseY);

            for (int i = 0; i < 4; i++)
            {
                int idx = i + scrollOffset;
                if (idx < entries.size())
                {
                    int entryY = getY() + ((i + 1) * ENTRY_HEIGHT);

                    T entry = entries.get(idx);
                    entry.render(graphics, getX() + 1, entryY, width - 2, entry == hoverEntry, getFGColor(), alpha);
                }
            }

            if (entries.size() > 4)
            {
                float scale = 4F / (float)entries.size();
                int scrollY = getY() + (int)(ENTRY_HEIGHT * scrollOffset * scale) + ENTRY_HEIGHT;
                int barHeight = (int)(ENTRY_HEIGHT * 4 * scale + 1);
                int scrollBotY = Math.min(scrollY + barHeight, getY() + ENTRY_HEIGHT + boxHeight - 2);

                graphics.fill(getX() + width - 5, scrollY,     getX() + width - 1, scrollBotY,     0xFF666666);
                graphics.fill(getX() + width - 4, scrollY + 1, getX() + width - 2, scrollBotY - 1, 0xFFAAAAAA);
            }

            graphics.pose().popPose();
        }
        else
        {
            graphics.blit(ICONS, getX() + width - 17, getY() + 6, 82, 20, 11, 7);
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
    public void onPress() { }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (active && mouseX >= getX() && mouseX <= getX() + width && mouseY >= getY() && mouseY <= getY() + getHeight())
        {
            int maxX = getX() + width - (entries.size() > 4 ? 5 : 0);
            int maxY = getY() + ENTRY_HEIGHT * Math.min(entries.size() + 1, 5);
            if (extended && mouseX < maxX && mouseY > (getY() + ENTRY_HEIGHT) && mouseY < maxY)
            {
                setSelected(getEntryAtPosition(mouseX, mouseY), true);
            }

            if ((mouseY < getY() + ENTRY_HEIGHT && mouseX < getX() + width) || mouseX < maxX)
            {
                toggleExtended();
            }

            playDownSound(Minecraft.getInstance().getSoundManager());

            return true;
        }

        extended = false;
        scrollOffset = 0;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        boolean hasFocused = extended && focused != null;
        if (active && visible && (isFocused() || hasFocused))
        {
            if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_KP_ENTER)
            {
                if (isFocused())
                {
                    toggleExtended();
                }
                else if (hasFocused)
                {
                    setSelected(focused, true);
                    toggleExtended();
                }
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }
        return false;
    }

    private void toggleExtended()
    {
        extended = !extended;
        scrollOffset = 0;
        if (extended && selected != null)
        {
            owner.setFocused(selected);
            scrollOffset = Math.min(entries.indexOf(selected), entries.size() - 4);
        }
        else if (!extended && focused != null)
        {
            focused = null;
            owner.setFocused(this);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta)
    {
        int maxY = getY() + ENTRY_HEIGHT * Math.min(entries.size() + 1, 5);
        if (extended && mouseX >= getX() && mouseX <= getX() + width && mouseY > getY() + ENTRY_HEIGHT && mouseY < maxY)
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
        return pMouseX >= getX() && pMouseY >= getY() && pMouseX < (getX() + width) && pMouseY < (getY() + getHeight());
    }

    private T getEntryAtPosition(double mouseX, double mouseY)
    {
        if (mouseX < getX() || mouseX > getX() + width || mouseY < (getY() + ENTRY_HEIGHT) || mouseY > (getY() + (ENTRY_HEIGHT * 5)))
        {
            return null;
        }

        double posY = mouseY - (getY() + ENTRY_HEIGHT);
        int idx = (int) (posY / ENTRY_HEIGHT) + scrollOffset;

        return idx < entries.size() ? entries.get(idx) : null;
    }

    void focusAndScrollTo(T entry)
    {
        focused = entry;

        int idx = entries.indexOf(entry);
        if (idx < 0 || idx >= entries.size())
        {
            return;
        }

        if (idx < scrollOffset)
        {
            scrollOffset = idx;
        }
        else if (idx > (scrollOffset + 3))
        {
            scrollOffset = idx - 3;
        }
    }

    public void addEntry(T entry)
    {
        entries.add(entry);
        entry.captureOwner(this);
    }

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
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event)
    {
        if (entries.isEmpty() || !extended || !(event instanceof FocusNavigationEvent.ArrowNavigation))
        {
            return super.nextFocusPath(event);
        }

        ScreenDirection dir = ((FocusNavigationEvent.ArrowNavigation) event).direction();
        if (dir.getAxis() == ScreenAxis.HORIZONTAL)
        {
            return null;
        }

        if (isFocused() && focused != null)
        {
            return ComponentPath.leaf(focused);
        }

        return switch (dir)
        {
            case UP -> ComponentPath.leaf(entries.get(entries.size() - 1));
            case DOWN -> ComponentPath.leaf(entries.get(0));
            default -> throw new IllegalStateException("Unreachable");
        };
    }

    public T getFocusNeighbour(T entry, ScreenDirection dir)
    {
        int idx = entries.indexOf(entry);
        return switch (dir)
        {
            case DOWN ->
            {
                if (idx < entries.size() - 1)
                {
                    yield entries.get(idx + 1);
                }
                yield entries.get(0);
            }
            case UP ->
            {
                if (idx > 0)
                {
                    yield entries.get(idx - 1);
                }
                yield entries.get(entries.size() - 1);
            }
            default -> null;
        };
    }



    public static class SelectionEntry<T extends SelectionEntry<T>> implements GuiEventListener
    {
        private final Component message;
        private SelectionWidget<T> owner;
        boolean focused = false;

        public SelectionEntry(Component message) { this.message = message; }

        public void render(GuiGraphics graphics, int x, int y, int width, boolean hovered, int fgColor, float alpha)
        {
            if (hovered || focused)
            {
                graphics.fill(x, y, x + width, y + ENTRY_HEIGHT, 0xFFA0A0A0);
            }

            Font font = Minecraft.getInstance().font;
            FormattedCharSequence text = Language.getInstance().getVisualOrder(FormattedText.composite(font.substrByWidth(message, width - 12)));
            graphics.drawString(font, text, x + 6, y + 6, fgColor | Mth.ceil(alpha * 255.0F) << 24);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers)
        {
            if (isFocused())
            {
                return owner.keyPressed(keyCode, scanCode, modifiers);
            }
            return false;
        }

        @Nullable
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent event)
        {
            if (isFocused() && event instanceof FocusNavigationEvent.ArrowNavigation arrowNav)
            {
                ScreenDirection dir = arrowNav.direction();
                //noinspection unchecked
                SelectionEntry<T> entry = owner.getFocusNeighbour((T) this, dir);
                if (entry != null)
                {
                    return ComponentPath.leaf(entry);
                }
            }
            return null;
        }

        @Override
        public final void setFocused(boolean focused)
        {
            this.focused = focused;
            if (focused)
            {
                //noinspection unchecked
                owner.focusAndScrollTo((T) this);
            }
        }

        @Override
        public final boolean isFocused()
        {
            return focused;
        }

        void captureOwner(SelectionWidget<T> owner)
        {
            this.owner = owner;
        }
    }
}