package io.github.xfacthd.atlasviewer.client.screen.stacking;

import io.github.xfacthd.atlasviewer.client.screen.stacking.IStackedScreen;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import java.util.*;

// Adapted from NeoForge's GUI stacking implementation
public final class ScreenStacker {
    private static final Deque<Screen> LAYERS = new ArrayDeque<>();

    public static void pushScreenLayer(Screen screen) {
        Minecraft mc = Minecraft.getInstance();
        Gui gui = mc.gui;

        if (gui.screen() != null) {
            LAYERS.push(gui.screen());
        }
        gui.atlasviewer$setScreenDirect(Objects.requireNonNull(screen));
        screen.init(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    public static void popScreenLayer() {
        Minecraft mc = Minecraft.getInstance();
        Gui gui = mc.gui;

        if (LAYERS.isEmpty()) {
            gui.setScreen(null);
        } else {
            popScreenLayer(gui);
            if (gui.screen() != null) {
                gui.screen().triggerImmediateNarration(false);
            }
        }
    }

    private static void popScreenLayer(Gui gui) {
        if (gui.screen() != null) {
            gui.screen().removed();
        }
        gui.atlasviewer$setScreenDirect(LAYERS.pop());
    }

    public static void clearScreenStack(Gui gui) {
        while (!LAYERS.isEmpty()) {
            popScreenLayer(gui);
        }
    }

    public static void onScreenInit(Screen screen) {
        if (screen instanceof IStackedScreen) {
            ScreenEvents.beforeExtract(screen).register((_, graphics, _, _, tickDelta) -> {
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
