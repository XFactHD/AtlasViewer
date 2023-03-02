package xfacthd.atlasviewer.client.screen.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import xfacthd.atlasviewer.client.util.ClientUtils;
import xfacthd.atlasviewer.client.util.TextureDrawer;

import java.util.ArrayList;
import java.util.List;

public final class MenuContainer
{
    private static final int BASE_OFFSET = 2;
    private static final int BUTTON_INTERVAL = 20 + BASE_OFFSET;

    private final Screen owner;
    private int x;
    private final int y;
    private final Button menuButton;
    private final boolean rightAlign;
    private final List<Button> buttons = new ArrayList<>();
    private int width;
    private int height = BASE_OFFSET;
    private int buttonWidth = 0;
    private boolean open = false;

    public MenuContainer(Screen owner, Button menuButton, boolean rightAlign)
    {
        this.owner = owner;
        this.x = menuButton.x;
        this.y = menuButton.y + menuButton.getHeight();
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

        button.y = y + BASE_OFFSET + (BUTTON_INTERVAL * buttons.size());
        button.visible = false;
        buttons.add(button);

        if (rightAlign)
        {
            x = menuButton.x + menuButton.getWidth() - width;
        }

        buttons.forEach(btn ->
        {
            btn.x = x + BASE_OFFSET;
            btn.setWidth(btnWidth);
        });
    }

    public void render(PoseStack poseStack)
    {
        if (open)
        {
            TextureDrawer.startColored();
            TextureDrawer.fillGuiColorBuffer(poseStack, owner, x, y, width, height, 0x666666FF);
            ClientUtils.drawColoredBox(owner, poseStack, x, y, width, height, 0x333333FF);
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
