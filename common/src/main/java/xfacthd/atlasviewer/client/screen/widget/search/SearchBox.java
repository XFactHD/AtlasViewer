package xfacthd.atlasviewer.client.screen.widget.search;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.client.util.IVisibilitySetter;

import java.util.function.Consumer;

public final class SearchBox extends AbstractWidget implements IVisibilitySetter
{
    static final int BUTTON_WIDTH = 63;
    static final int PADDING = 2;
    private static final long DEBOUNCE_DELAY_MS = 250;

    private final SearchHandler handler;
    final SearchEditBox editBox;
    final SearchButton button;
    private boolean changed = false;
    private String lastQuery = "";
    private long lastChange = 0;

    public SearchBox(int x, int y, int width, int height, @Nullable SearchBox prev, SearchHandler handler, Consumer<AbstractWidget> registrar)
    {
        super(x, y, width, height, Component.empty());
        this.handler = handler;
        this.editBox = new SearchEditBox(x, y, width - BUTTON_WIDTH - PADDING, height, prev != null ? prev.editBox : null);
        this.button = new SearchButton(x + BUTTON_WIDTH + PADDING, y, BUTTON_WIDTH, height, handler);
        this.editBox.setResponder(this::onSearchChanged);
        registrar.accept(editBox);
        registrar.accept(button);
    }

    private void onSearchChanged(String text)
    {
        changed = true;
        lastQuery = text;
        lastChange = System.currentTimeMillis();
        if (lastQuery.isEmpty())
        {
            button.active = false;
        }
    }

    public void tick()
    {
        if (changed && System.currentTimeMillis() - lastChange > DEBOUNCE_DELAY_MS)
        {
            changed = false;
            handler.updateSearch(lastQuery);
            button.active = !lastQuery.isEmpty();
        }
    }

    public void clear()
    {
        editBox.setValue("");
        button.active = false;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) { }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) { }

    @Override
    public void setX(int x)
    {
        super.setX(x);
        editBox.setX(x);
        button.setX(x + width - BUTTON_WIDTH);
    }

    @Override
    public void setY(int y)
    {
        super.setY(y);
        editBox.setY(y);
        button.setY(y);
    }

    @Override
    public void setWidth(int width)
    {
        super.setWidth(width);
        editBox.setWidth(width - BUTTON_WIDTH - PADDING);
    }

    @Override
    public void setHeight(int height)
    {
        super.setHeight(height);
        editBox.setHeight(height);
        button.setHeight(height);
    }

    @Override
    public void atlasviewer$setVisible(boolean visible)
    {
        this.visible = visible;
        editBox.visible = visible;
        button.visible = visible;
    }
}
