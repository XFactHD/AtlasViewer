package io.github.xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenAxis;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class SelectionWidget<T extends SelectionWidget.SelectionEntry<T>> extends AbstractButton {
    private static final Identifier ARROW_UP = Identifier.withDefaultNamespace("transferable_list/move_up");
    private static final Identifier ARROW_DOWN = Identifier.withDefaultNamespace("transferable_list/move_down");
    private static final int MAX_VISIBLE_ENTRIES = 4;
    private static final int AUTOSCROLL_OFFSET = (MAX_VISIBLE_ENTRIES - 1) / 2;
    private static final int BORDER = 1;
    private static final int CONTENT_PADDING = 6;
    // The arrow sprites have whitespace around the content, coordinates need to be offset accordingly
    private static final int ARROW_UP_OFF_X = 18;
    private static final int ARROW_UP_OFF_Y = 5;
    private static final int ARROW_DOWN_OFF_X = 18;
    private static final int ARROW_DOWN_OFF_Y = 20;
    private static final int ARROW_SIZE = 32;
    private static final int BASE_HEIGHT = 20;
    private static final int ENTRY_HEIGHT = 20;
    private static final int MAX_LIST_HEIGHT = ENTRY_HEIGHT * MAX_VISIBLE_ENTRIES;
    private static final int SCROLLER_WIDTH = 4;
    private static final int SCROLLER_BOX_WIDTH = SCROLLER_WIDTH + BORDER * 2;
    private static final int SCROLLER_HEIGHT = 24;
    private final Screen owner;
    private final Component title;
    @Nullable
    private final Consumer<T> selectCallback;
    private final List<T> entries = new ArrayList<>();
    @Nullable
    private T focused = null;
    @Nullable
    private T selected = null;
    private boolean extended = false;
    private int scrollOffset = 0;
    private boolean dragging = false;

    public SelectionWidget(Screen owner, int x, int y, int width, Component title, @Nullable Consumer<T> selectCallback) {
        super(x, y, width, BASE_HEIGHT, Component.empty());
        this.owner = owner;
        this.title = title;
        this.selectCallback = selectCallback;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(active, isHoveredOrFocused()), getX(), getY(), getWidth(), BASE_HEIGHT, ARGB.white(alpha));

        int textColor = ARGB.color(alpha, active ? 0xFFFFFF : 0xA0A0A0);

        if (selected != null) {
            boolean entryFocused = selected.isFocused();
            selected.focused = false;
            selected.render(graphics, getX(), getY(), width, false, false, textColor);
            selected.focused = entryFocused;
        } else {
            Font font = Minecraft.getInstance().font;
            graphics.text(font, title, getX() + CONTENT_PADDING, getY() + CONTENT_PADDING, textColor);
        }

        if (extended) {
            int listY = getListY();
            int boxHeight = getListHeight(1) + BORDER * 2;
            boolean scrollable = hasScrollBar();

            int frameY = listY - BORDER;
            graphics.fill(getX(),          frameY,          getRight(),          frameY + boxHeight,          0xFFFFFFFF);
            graphics.fill(getX() + BORDER, frameY + BORDER, getRight() - BORDER, frameY + boxHeight - BORDER, 0xFF000000);
            if (scrollable) {
                graphics.verticalLine(getRight() - SCROLLER_BOX_WIDTH, frameY, frameY + boxHeight, 0xFFFFFFFF);
            }

            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW_UP, getRight() - 17 - ARROW_UP_OFF_X, getY() + 6 - ARROW_UP_OFF_Y, 32, 32);

            T hoverEntry = getEntryAtPosition(mouseX, mouseY);
            int entryWidth = width - (BORDER * 2) - (scrollable ? (SCROLLER_WIDTH + BORDER) : 0);

            for (int i = 0; i < MAX_VISIBLE_ENTRIES; i++) {
                int idx = i + scrollOffset;
                if (idx >= entries.size()) {
                    break;
                }

                T entry = entries.get(idx);
                int entryY = listY + (ENTRY_HEIGHT * i);
                entry.render(graphics, getX() + BORDER, entryY, entryWidth, entry == hoverEntry, entry == selected, textColor);
            }

            if (scrollable) {
                float scrollFactor = (float) scrollOffset / (entries.size() - MAX_VISIBLE_ENTRIES);
                int scrollMinX = getRight() - BORDER - SCROLLER_WIDTH;
                int scrollMaxX = scrollMinX + SCROLLER_WIDTH;
                int scrollMinY = listY + (int) (scrollFactor * (MAX_LIST_HEIGHT - SCROLLER_HEIGHT));
                int scrollMaxY = scrollMinY + SCROLLER_HEIGHT;

                graphics.fill(scrollMinX,          scrollMinY,          scrollMaxX,          scrollMaxY,          0xFF666666);
                graphics.fill(scrollMinX + BORDER, scrollMinY + BORDER, scrollMaxX - BORDER, scrollMaxY - BORDER, 0xFFAAAAAA);
            }
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW_DOWN, getRight() - 17 - ARROW_DOWN_OFF_X, getY() + 6 - ARROW_DOWN_OFF_Y, ARROW_SIZE, ARROW_SIZE);
        }
    }

    @Override
    public int getHeight() {
        if (extended) {
            return BASE_HEIGHT + getListHeight(1) + BORDER;
        }
        return BASE_HEIGHT;
    }

    @Override
    public void onPress(InputWithModifiers input) { }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (!active || !isMouseOver(event.x(), event.y())) {
            setExtended(false);
            return super.mouseClicked(event, doubleClick);
        }
        if (event.y() < getListY()) {
            toggleExtended();
            playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }
        if (!extended) {
            return false;
        }

        boolean scrollable = hasScrollBar();
        int minX = getX() + BORDER;
        int maxX = getRight() - BORDER;
        int maxElemX = scrollable ? (maxX - SCROLLER_WIDTH - BORDER) : maxX;
        int maxY = getListY() + getListHeight(0);
        if (event.x() < minX || event.x() > maxX || event.y() > maxY) {
            return true;
        }
        if (scrollable && event.x() >= maxX - SCROLLER_WIDTH) {
            dragging = true;
            return true;
        } else if (event.x() < maxElemX) {
            setSelected(getEntryAtPosition(event.x(), event.y()), true);
            playDownSound(Minecraft.getInstance().getSoundManager());
            toggleExtended();
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        dragging = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            int minY = getListY();
            int maxY = getBottom() - BORDER;
            int relY = (int) Math.round(Math.clamp(event.y(), minY, maxY) - minY);
            int maxOffset = entries.size() - MAX_VISIBLE_ENTRIES;
            float factor = (relY - (SCROLLER_HEIGHT / 2F)) / (MAX_LIST_HEIGHT - SCROLLER_HEIGHT);
            scrollOffset = (int) Math.clamp(factor * maxOffset, 0, maxOffset);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        boolean hasFocused = extended && focused != null;
        if (active && visible && (isFocused() || hasFocused)) {
            if (event.isSelection()) {
                if (isFocused()) {
                    toggleExtended();
                } else if (hasFocused) {
                    setSelected(focused, true);
                    toggleExtended();
                }
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }
        return false;
    }

    private int getListY() {
        return getY() + BASE_HEIGHT;
    }

    private int getListHeight(int min) {
        return ENTRY_HEIGHT * Math.clamp(entries.size(), min, MAX_VISIBLE_ENTRIES);
    }

    private boolean hasScrollBar() {
        return entries.size() > MAX_VISIBLE_ENTRIES;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        if (this.extended != extended) {
            toggleExtended();
        }
    }

    private void toggleExtended() {
        extended = !extended;
        scrollOffset = 0;
        if (extended && selected != null) {
            owner.setFocused(selected);
            scrollTo(selected, true);
        } else if (!extended && focused != null) {
            focused = null;
            owner.setFocused(this);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        int minY = getListY();
        int maxY = minY + getListHeight(0);
        if (extended && mouseX >= getX() && mouseX <= getRight() && mouseY > minY && mouseY < maxY) {
            if (deltaY < 0 && scrollOffset < entries.size() - MAX_VISIBLE_ENTRIES) {
                scrollOffset++;
            } else if (deltaY > 0 && scrollOffset > 0) {
                scrollOffset--;
            }
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, deltaX, deltaY);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!active || !visible) {
            return false;
        }
        return mouseX >= getX() && mouseY >= getY() && mouseX < getRight() && mouseY < getBottom();
    }

    private @Nullable T getEntryAtPosition(double mouseX, double mouseY) {
        int maxX = getRight() - (hasScrollBar() ? SCROLLER_BOX_WIDTH : BORDER);
        int minY = getListY();
        int maxY = minY + getListHeight(0);
        if (mouseX < getX() + BORDER || mouseX >= maxX || mouseY < minY || mouseY > maxY) {
            return null;
        }

        double posY = mouseY - minY;
        int idx = (int) (posY / ENTRY_HEIGHT) + scrollOffset;
        return idx < entries.size() ? entries.get(idx) : null;
    }

    private void focusAndScrollTo(T entry) {
        focused = entry;
        scrollTo(entry, false);
    }

    private void scrollTo(T entry, boolean initial) {
        int idx = entries.indexOf(entry);
        if (idx < 0 || idx >= entries.size()) {
            return;
        }

        if (initial) {
            scrollOffset = Math.clamp(idx - AUTOSCROLL_OFFSET, 0, entries.size() - (MAX_VISIBLE_ENTRIES));
        } else if (idx < scrollOffset) {
            scrollOffset = idx;
        } else if (idx > (scrollOffset + 3)) {
            scrollOffset = idx - 3;
        }
    }

    public void addEntry(T entry) {
        entries.add(entry);
        entry.captureOwner(this);
    }

    public void setSelected(@Nullable T selected, boolean notify) {
        this.selected = selected;
        if (notify && selectCallback != null && selected != null) {
            selectCallback.accept(selected);
        }
    }

    public @Nullable T getSelected() {
        return selected;
    }

    public Stream<T> stream() {
        return entries.stream();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) { }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (entries.isEmpty() || !extended || !(event instanceof FocusNavigationEvent.ArrowNavigation)) {
            return super.nextFocusPath(event);
        }

        ScreenDirection dir = ((FocusNavigationEvent.ArrowNavigation) event).direction();
        if (dir.getAxis() == ScreenAxis.HORIZONTAL) {
            return null;
        }

        if (isFocused() && focused != null) {
            return ComponentPath.leaf(focused);
        }

        return switch (dir) {
            case UP -> ComponentPath.leaf(entries.getLast());
            case DOWN -> ComponentPath.leaf(entries.getFirst());
            default -> throw new IllegalStateException("Unreachable");
        };
    }

    public @Nullable T getFocusNeighbour(T entry, ScreenDirection dir) {
        int idx = entries.indexOf(entry);
        return switch (dir) {
            case DOWN -> {
                if (idx < entries.size() - 1) {
                    yield entries.get(idx + 1);
                }
                yield entries.getFirst();
            }
            case UP -> {
                if (idx > 0) {
                    yield entries.get(idx - 1);
                }
                yield entries.getLast();
            }
            default -> null;
        };
    }

    public static class SelectionEntry<T extends SelectionEntry<T>> implements GuiEventListener {
        private final Component message;
        @Nullable
        private SelectionWidget<T> owner = null;
        boolean focused = false;

        public SelectionEntry(Component message) {
            this.message = message;
        }

        public void render(GuiGraphicsExtractor graphics, int x, int y, int width, boolean hovered, boolean selected, int textColor) {
            if (hovered || focused) {
                graphics.fill(x, y, x + width, y + ENTRY_HEIGHT, 0xFFA0A0A0);
            } else if (selected) {
                graphics.fill(x, y, x + width, y + ENTRY_HEIGHT, 0xFF505050);
            }

            Font font = Minecraft.getInstance().font;
            FormattedCharSequence text = Language.getInstance().getVisualOrder(font.substrByWidth(message, width - (CONTENT_PADDING * 2)));
            graphics.text(font, text, x + CONTENT_PADDING, y + CONTENT_PADDING, textColor);
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (isFocused()) {
                return Objects.requireNonNull(owner).keyPressed(event);
            }
            return false;
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
            if (isFocused() && event instanceof FocusNavigationEvent.ArrowNavigation(ScreenDirection dir, _)) {
                SelectionEntry<T> entry = Objects.requireNonNull(owner).getFocusNeighbour((T) this, dir);
                if (entry != null) {
                    return ComponentPath.leaf(entry);
                }
            }
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public final void setFocused(boolean focused) {
            this.focused = focused;
            if (focused) {
                Objects.requireNonNull(owner).focusAndScrollTo((T) this);
            }
        }

        @Override
        public final boolean isFocused() {
            return focused;
        }

        void captureOwner(SelectionWidget<T> owner) {
            this.owner = owner;
        }
    }
}
