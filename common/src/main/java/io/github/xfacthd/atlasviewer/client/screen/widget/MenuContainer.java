package io.github.xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import io.github.xfacthd.atlasviewer.client.util.ClientUtils;

public final class MenuContainer extends GridLayout {
    private static final int PADDING = 2;

    private final int originX;
    private final int originWidth;
    private final Button menuButton;
    private final boolean rightAlign;
    private int nextRow = 1;
    private boolean open = false;

    public MenuContainer(Button menuButton, boolean rightAlign) {
        super(menuButton.getX(), menuButton.getY() + Button.DEFAULT_HEIGHT);
        this.originX = menuButton.getX();
        this.originWidth = menuButton.getWidth();
        this.menuButton = menuButton;
        this.rightAlign = rightAlign;
        defaultCellSetting().padding(PADDING);
    }

    @Override
    public void arrangeElements() {
        super.arrangeElements();
        if (rightAlign) {
            setX(originX + originWidth - getWidth());
        }
        visitWidgets(widget -> widget.setWidth(getWidth() - (PADDING * 2)));
        setOpen(this, false);
    }

    public void addMenuEntry(LayoutElement element) {
        addChild(element, nextRow, 0);
        // Remove top padding after first entry for uniform distance
        defaultCellSetting().paddingTop(0);
        nextRow++;
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (open) {
            graphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0xFF666666);
            ClientUtils.drawColoredBox(graphics, getX(), getY(), getWidth(), getHeight(), 0xFF333333);
        }
    }

    public void toggleOpen() {
        setOpen(!open);
    }

    public void setOpen(boolean open) {
        if (this.open != open) {
            this.open = open;
            setOpen(this, open);
        }
    }

    private void setOpen(LayoutElement element, boolean open) {
        element.visitWidgets(widget -> {
            if (widget != menuButton) {
                widget.atlasviewer$setVisible(open);
            }
        });
        if (element instanceof Layout layout) {
            layout.visitChildren(childElem -> setOpen(childElem, open));
        }
    }

    public boolean isOpen() {
        return open;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (mouseX >= getX() && mouseY >= getY() && mouseX <= getX() + getWidth() && mouseY <= getY() + getHeight()) {
            return true;
        }
        return menuButton.isMouseOver(mouseX, mouseY);
    }
}
