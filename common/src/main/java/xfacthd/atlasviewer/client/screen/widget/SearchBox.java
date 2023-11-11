package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

public final class SearchBox extends EditBox
{
    private static final Component TITLE_SEARCH_BAR = Component.translatable("btn.atlasviewer.search");
    private static final int NUMBER_WIDTH = 63;
    private static final long DEBOUNCE_DELAY_MS = 250;

    private final Font font;
    private final SearchHandler handler;
    private final Rect2i btnRect = new Rect2i(0, 0, 0, 0);
    private int actualWidth;
    private boolean wasEmpty = true;
    private boolean changed = false;
    private long lastChange = 0;

    public SearchBox(Font font, int x, int y, int w, int h, @Nullable SearchBox prev, SearchHandler handler)
    {
        super(font, x, y, w - NUMBER_WIDTH, h, prev, TITLE_SEARCH_BAR);
        this.font = font;
        this.handler = handler;
        this.actualWidth = w;
        updateBtnRect();
        setHint(TITLE_SEARCH_BAR);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        if (!visible) return;

        super.render(graphics, mouseX, mouseY, partialTick);

        boolean canInteract = !getValue().isEmpty() && (!wasEmpty || !changed);
        ResourceLocation tex = getButtonTexture(canInteract, mouseX, mouseY);
        graphics.blitSprite(tex, btnRect.getX(), btnRect.getY(), btnRect.getWidth(), btnRect.getHeight());

        if (canInteract)
        {
            int count = handler.getResultCount();
            int focusedIdx = handler.getFocusedResultIndex();
            String text = focusedIdx > -1 ? "%d/%d".formatted((focusedIdx + 1), count) : Integer.toString(count);

            graphics.pose().pushPose();
            int length = font.width(text);
            boolean scale = length > (NUMBER_WIDTH - 9);
            float x = getX() + actualWidth - 3 - (font.width(text) * (scale ? .5F : 1F));
            float y = getY() + 5F + (scale ? 2F : 0F);
            graphics.pose().translate(x, y, 0);
            if (scale)
            {
                graphics.pose().scale(.5F, .5F, 1);
            }
            graphics.drawString(font, text, 0, 0, count > 0 ? 0xFFFFFFFF : 0xFF0000);
            graphics.pose().popPose();
        }
    }

    private ResourceLocation getButtonTexture(boolean canInteract, int mouseX, int mouseY)
    {
        try
        {
            // FIXME: Remove reflection crap when an AT works for this field on Neo
            WidgetSprites sprites = (WidgetSprites) AbstractButton.class.getDeclaredField("SPRITES").get(null);
            return sprites.get(canInteract, canInteract && btnRect.contains(mouseX, mouseY));
        }
        catch (IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }
        //return AbstractButton.SPRITES.get(canInteract, canInteract && btnRect.contains(mouseX, mouseY));
    }

    public void tick()
    {
        if (changed && System.currentTimeMillis() - lastChange > DEBOUNCE_DELAY_MS)
        {
            changed = false;
            wasEmpty = getValue().isEmpty();
            handler.updateSearch(getValue());
        }
    }

    @Override
    protected void onValueChange(String text)
    {
        changed = true;
        lastChange = System.currentTimeMillis();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int btn)
    {
        if (btn == GLFW.GLFW_MOUSE_BUTTON_RIGHT && clicked(mouseX, mouseY))
        {
            setValue("");
            return true;
        }
        else if (btn == GLFW.GLFW_MOUSE_BUTTON_LEFT && btnRect.contains((int) mouseX, (int) mouseY))
        {
            if (!getValue().isEmpty() && (!wasEmpty || !changed))
            {
                handler.jumpToNextResult();
                playDownSound(Minecraft.getInstance().getSoundManager());
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, btn);
    }

    @Override
    public int getWidth()
    {
        return actualWidth;
    }

    @Override
    public void setX(int x)
    {
        super.setX(x);
        updateBtnRect();
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width - NUMBER_WIDTH);
        this.actualWidth = width;
        updateBtnRect();
    }

    private void updateBtnRect()
    {
        btnRect.setPosition(getX() + width + 2, getY() - 1);
        btnRect.setWidth(actualWidth - width - 1);
        btnRect.setHeight(height + 2);
    }



    public interface SearchHandler
    {
        int getResultCount();

        void updateSearch(String text);

        void jumpToNextResult();

        int getFocusedResultIndex();
    }
}
