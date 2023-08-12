package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public final class SearchBox extends EditBox
{
    private static final Component TITLE_SEARCH_BAR = Component.translatable("btn.atlasviewer.search");
    private static final int NUMBER_WIDTH = 30;
    private static final long DEBOUNCE_DELAY_MS = 250;

    private final Font font;
    private final SearchHandler handler;
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
        setHint(TITLE_SEARCH_BAR);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.render(graphics, mouseX, mouseY, partialTick);

        if (visible && !getValue().isEmpty() && (!wasEmpty || !changed))
        {
            int count = handler.getResultCount();
            String text = Integer.toString(count);
            int length = font.width(text);
            int x = getX() + actualWidth - 1 - length;
            int y = getY() + 5;
            graphics.drawString(font, text, x, y, count > 0 ? 0xFFFFFFFF : 0xFF0000);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
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
    public int getWidth()
    {
        return actualWidth;
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width - NUMBER_WIDTH);
        this.actualWidth = width;
    }



    public interface SearchHandler
    {
        int getResultCount();

        void updateSearch(String text);
    }
}
