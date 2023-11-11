package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.TextureDrawer;

import java.util.ArrayList;
import java.util.List;

public final class MenuContainer
{
    private static final int BASE_OFFSET = 2;
    private static final int ENTRY_INTERVAL = Button.DEFAULT_HEIGHT + BASE_OFFSET;

    private int x;
    private final int y;
    private final Button menuButton;
    private final boolean rightAlign;
    private final List<MenuEntry> entries = new ArrayList<>();
    private int width;
    private int height = BASE_OFFSET;
    private int buttonWidth = 0;
    private boolean open = false;

    public MenuContainer(Button menuButton, boolean rightAlign)
    {
        this.x = menuButton.getX();
        this.y = menuButton.getY() + menuButton.getHeight();
        this.width = menuButton.getWidth();
        this.menuButton = menuButton;
        this.rightAlign = rightAlign;
    }

    public void addMenuEntry(AbstractWidget widget)
    {
        addMenuEntry(widget, 0, 0);
    }

    public void addMenuEntry(AbstractWidget widget, int baseX, int baseY)
    {
        int btnWidth = Math.max(buttonWidth, widget.getWidth());
        if (btnWidth != buttonWidth)
        {
            buttonWidth = btnWidth;
        }

        height += ENTRY_INTERVAL;
        width = Math.max(width, btnWidth + (BASE_OFFSET * 2));

        widget.setY(baseY + y + BASE_OFFSET + (ENTRY_INTERVAL * entries.size()));
        widget.visible = false;
        entries.add(new MenuEntry(widget, baseX, baseY));

        if (rightAlign)
        {
            x = menuButton.getX() + menuButton.getWidth() - width;
        }

        entries.forEach(entry ->
        {
            entry.widget.setX(entry.baseX + x + BASE_OFFSET);
            entry.widget.setWidth(btnWidth - (2 * entry.baseX));
        });
    }

    public void render(PoseStack poseStack)
    {
        if (open)
        {
            TextureDrawer.startColored();
            TextureDrawer.fillGuiColorBuffer(poseStack, x, y, 0, width, height, 0x666666FF);
            ClientUtils.drawColoredBox(poseStack, x, y, 0, width, height, 0x333333FF);
            TextureDrawer.end();
        }
    }

    public void toggleOpen()
    {
        setOpen(!open);
    }

    public void setOpen(boolean open)
    {
        if (this.open != open)
        {
            this.open = open;
            entries.forEach(entry -> entry.widget.visible = open);
        }
    }

    public boolean isOpen()
    {
        return open;
    }

    public boolean isMouseOver(double mouseX, double mouseY)
    {
        if (mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height)
        {
            return true;
        }
        return menuButton.isMouseOver(mouseX, mouseY);
    }



    private record MenuEntry(AbstractWidget widget, int baseX, int baseY) { }
}
