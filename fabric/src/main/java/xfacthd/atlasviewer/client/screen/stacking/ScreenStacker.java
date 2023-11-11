package xfacthd.atlasviewer.client.screen.stacking;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;

// Adapted from NeoForge's GUI stacking implementation
public final class ScreenStacker
{
    private static final Deque<Screen> LAYERS = new ArrayDeque<>();

    public static void pushScreenLayer(Screen screen)
    {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen != null)
        {
            LAYERS.push(mc.screen);
        }
        mc.screen = Objects.requireNonNull(screen);
        screen.init(mc, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        mc.getNarrator().sayNow(screen.getNarrationMessage());
    }

    public static void popScreenLayer()
    {
        Minecraft mc = Minecraft.getInstance();

        if (LAYERS.isEmpty())
        {
            mc.setScreen(null);
            return;
        }

        popScreenLayer(mc);
        if (mc.screen != null)
        {
            mc.getNarrator().sayNow(mc.screen.getNarrationMessage());
        }
    }

    private static void popScreenLayer(Minecraft mc)
    {
        if (mc.screen != null)
        {
            mc.screen.removed();
        }
        mc.screen = LAYERS.pop();
    }

    public static void clearScreenStack(Minecraft mc)
    {
        while (!LAYERS.isEmpty())
        {
            if (mc.screen != null)
            {
                mc.screen.removed();
            }
            mc.screen = LAYERS.pop();
        }
    }

    public static float getGuiFarPlane()
    {
        // 1000 units for the overlay background,
        // and 10000 units for each layered Screen,
        return 1000F + 10000F * (1 + LAYERS.size());
    }

    public static boolean isNonEmpty()
    {
        return !LAYERS.isEmpty();
    }

    public static void onScreenInit(Screen screen)
    {
        if (screen instanceof IStackedScreen)
        {
            ScreenEvents.beforeRender(screen).register((topScreen, guiGraphics, mouseX, mouseY, tickDelta) ->
            {
                guiGraphics.pose().pushPose();
                for (Iterator<Screen> it = LAYERS.descendingIterator(); it.hasNext();)
                {
                    Screen layer = it.next();
                    layer.renderWithTooltip(guiGraphics, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta);
                    guiGraphics.pose().translate(0, 0, 2000);
                }
            });

            ScreenEvents.afterRender(screen).register((topScreen, guiGraphics, mouseX, mouseY, tickDelta) ->
                    guiGraphics.pose().popPose()
            );
        }
    }

    public static void onScreenResize(Minecraft mc, int scaledWidth, int scaledHeight)
    {
        LAYERS.forEach(screen -> screen.resize(mc, scaledWidth, scaledHeight));
    }



    private ScreenStacker() { }
}
