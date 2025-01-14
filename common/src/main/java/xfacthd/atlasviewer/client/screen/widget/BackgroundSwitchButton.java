package xfacthd.atlasviewer.client.screen.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.util.NineSlice;

import java.util.Objects;

public final class BackgroundSwitchButton extends Button
{
    public static final Component TITLE = Component.translatable("btn.atlasviewer.switch_background");
    private static final ResourceLocation BG_SELECTED = AtlasViewer.rl("icon_background_selected");
    private static final ResourceLocation BG_UNSELECTED = AtlasViewer.rl("icon_background_unselected");
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
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        for (int i = 0; i < Type.VALUES.length; i++)
        {
            Type type = Type.VALUES[i];
            ResourceLocation bgTex = this.type == type ? BG_SELECTED : BG_UNSELECTED;
            ResourceLocation tex = type.sprite;
            int x = getX() + getWidth() - 1 - (ICON_BG_SIZE + 1) * (Type.VALUES.length - i);
            int y = getY() + 4;
            graphics.blitSprite(bgTex, x - 1, y - 1, 0, ICON_BG_SIZE, ICON_BG_SIZE);
            graphics.blit(tex, x, y, 0, 0, 0, ICON_SIZE, ICON_SIZE, type.texWidth, type.texHeight);
        }
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color)
    {
        int maxX = getX() + (getWidth() - 3 - ICON_TOTAL_WIDTH) - 2;
        renderScrollingString(graphics, font, getMessage(), getX() + 2, getY(), maxX, getY() + getHeight(), color);
    }

    @Override
    public void onPress()
    {
        type = Screen.hasShiftDown() ? type.previous() : type.next();
    }

    public Type getSelectedType()
    {
        return type;
    }

    public enum Type
    {
        CHECKER(AtlasViewer.rl("textures/gui/checker.png"), 256, 256, new NineSlice(0, 0, 256, 256, 256, 256, 0)),
        DARK(AtlasViewer.rl("textures/gui/dark.png"), 16, 16, null),
        WHITE(AtlasViewer.rl("textures/gui/white.png"), 16, 16, null),
        ;

        private static final Type[] VALUES = values();

        private final ResourceLocation sprite;
        private final int texWidth;
        private final int texHeight;
        @Nullable
        private final NineSlice nineSlice;

        Type(ResourceLocation sprite, int texWidth, int texHeight, @Nullable NineSlice nineSlice)
        {
            this.sprite = sprite;
            this.texWidth = texWidth;
            this.texHeight = texHeight;
            this.nineSlice = nineSlice;
        }

        public ResourceLocation getLocation()
        {
            return sprite;
        }

        @Nullable
        public NineSlice getNineSlice()
        {
            return nineSlice;
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
