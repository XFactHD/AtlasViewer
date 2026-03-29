package xfacthd.atlasviewer.client.screen.stacking;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;

// Adapted from NeoForge's GUI stacking implementation
public final class ScreenStacker {
    private static final Deque<Screen> LAYERS = new ArrayDeque<>();

    public static void pushScreenLayer(Screen screen) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.screen != null) {
            LAYERS.push(mc.screen);
        }
        mc.screen = Objects.requireNonNull(screen);
        screen.init(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        mc.getNarrator().saySystemNow(screen.getNarrationMessage());
    }

    public static void popScreenLayer() {
        Minecraft mc = Minecraft.getInstance();

        if (LAYERS.isEmpty()) {
            mc.setScreen(null);
            return;
        }

        popScreenLayer(mc);
        if (mc.screen != null) {
            mc.getNarrator().saySystemNow(mc.screen.getNarrationMessage());
        }
    }

    private static void popScreenLayer(Minecraft mc) {
        if (mc.screen != null) {
            mc.screen.removed();
        }
        mc.screen = LAYERS.pop();
    }

    public static void clearScreenStack(Minecraft mc) {
        while (!LAYERS.isEmpty()) {
            if (mc.screen != null) {
                mc.screen.removed();
            }
            mc.screen = LAYERS.pop();
        }
    }

    public static void onScreenInit(Screen screen) {
        if (screen instanceof IStackedScreen) {
            ScreenEvents.beforeExtract(screen).register((_, graphics, _, _, tickDelta) ->
            {
                for (Iterator<Screen> it = LAYERS.descendingIterator(); it.hasNext(); ) {
                    it.next().extractRenderStateWithTooltipAndSubtitles(graphics, Integer.MAX_VALUE, Integer.MAX_VALUE, tickDelta);
                    graphics.nextStratum();
                }
            });
        }
    }

    public static void onScreenResize(int scaledWidth, int scaledHeight) {
        LAYERS.forEach(screen -> screen.resize(scaledWidth, scaledHeight));
    }

    private ScreenStacker() { }
}
