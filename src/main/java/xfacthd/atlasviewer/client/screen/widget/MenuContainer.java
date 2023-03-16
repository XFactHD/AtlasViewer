package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.TextureDrawer;

import java.util.ArrayList;
import java.util.List;

public final class MenuContainer
{
    private static final int BASE_OFFSET = 2;
    private static final int BUTTON_INTERVAL = Button.DEFAULT_HEIGHT + BASE_OFFSET;

    private int x;
    private final int y;
    private final Button menuButton;
    private final boolean rightAlign;
    private final List<Button> buttons = new ArrayList<>();
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

    public void addButton(Button button)
    {
        int btnWidth = Math.max(buttonWidth, button.getWidth());
        if (btnWidth != buttonWidth)
        {
            buttonWidth = btnWidth;
        }

        height += BUTTON_INTERVAL;
        width = Math.max(width, btnWidth + (BASE_OFFSET * 2));

        int btnY = y + BASE_OFFSET + (BUTTON_INTERVAL * buttons.size());
        button.setY(btnY);
        button.visible = false;
        buttons.add(button);

        if (rightAlign)
        {
            x = menuButton.getX() + menuButton.getWidth() - width;
        }

        buttons.forEach(btn ->
        {
            btn.setX(x + BASE_OFFSET);
            btn.setWidth(btnWidth);
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
            buttons.forEach(btn -> btn.visible = open);
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
}
