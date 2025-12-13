package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import xfacthd.atlasviewer.AtlasViewer;

import java.util.Objects;

public final class BackgroundSwitchButton extends Button.Plain
{
    public static final Component TITLE = Component.translatable("btn.atlasviewer.switch_background");
    private static final Identifier BG_SELECTED = AtlasViewer.rl("icon_background_selected");
    private static final Identifier BG_UNSELECTED = AtlasViewer.rl("icon_background_unselected");
    private static final int ICON_BG_SIZE = 14;
    private static final int ICON_SIZE = 12;
    private static final int ICON_TOTAL_WIDTH = (ICON_BG_SIZE + 1) * Type.VALUES.length - 1;

    private Type type;

    public BackgroundSwitchButton(int x, int y, int width, @Nullable Type type)
    {
        super(x, y, width, DEFAULT_HEIGHT, TITLE, btn -> {}, DEFAULT_NARRATION);
        this.type = Objects.requireNonNullElse(type, Type.CHECKER);
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderContents(graphics, mouseX, mouseY, partialTick);
        for (int i = 0; i < Type.VALUES.length; i++)
        {
            Type type = Type.VALUES[i];
            Identifier bgTex = this.type == type ? BG_SELECTED : BG_UNSELECTED;
            Identifier tex = type.sprite;
            int x = getX() + getWidth() - 1 - (ICON_BG_SIZE + 1) * (Type.VALUES.length - i);
            int y = getY() + 4;
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, bgTex, x - 1, y - 1, ICON_BG_SIZE, ICON_BG_SIZE);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, tex, x, y, ICON_SIZE, ICON_SIZE);
        }
    }

    @Override
    protected void renderDefaultLabel(ActiveTextCollector textCollector)
    {
        int maxX = getX() + (getWidth() - 3 - ICON_TOTAL_WIDTH) - 2;
        textCollector.acceptScrollingWithDefaultCenter(getMessage(), getX() + 2, maxX, getY(), getY() + getHeight());
    }

    @Override
    public void onPress(InputWithModifiers input)
    {
        type = Minecraft.getInstance().hasShiftDown() ? type.previous() : type.next();
    }

    public Type getSelectedType()
    {
        return type;
    }

    public enum Type
    {
        CHECKER(AtlasViewer.rl("checker")),
        DARK(AtlasViewer.rl("dark")),
        WHITE(AtlasViewer.rl("white")),
        ;

        private static final Type[] VALUES = values();

        private final Identifier sprite;

        Type(Identifier sprite)
        {
            this.sprite = sprite;
        }

        public Identifier getSprite()
        {
            return sprite;
        }

        Type next()
        {
            return VALUES[(ordinal() + 1) % VALUES.length];
        }

        Type previous()
        {
            return VALUES[(ordinal() + VALUES.length - 1) % VALUES.length];
        }
    }
}
