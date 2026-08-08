package io.github.xfacthd.atlasviewer;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import io.github.xfacthd.atlasviewer.client.AVClient;
import io.github.xfacthd.atlasviewer.client.sourcehandling.SpriteSourceManager;

@Mod(value = AtlasViewer.MOD_ID, dist = Dist.CLIENT)
public final class AtlasViewerNeoForge {
    public AtlasViewerNeoForge(IEventBus modBus) {
        modBus.addListener(AtlasViewerNeoForge::onRegisterKeyMappings);
        modBus.addListener(AtlasViewerNeoForge::onRegisterReloadListeners);

        NeoForge.EVENT_BUS.addListener(AtlasViewerNeoForge::onClientTick);
    }

    private static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        AVClient.onRegisterKeyMappings(event::registerCategory, event::register);
    }

    private static void onRegisterReloadListeners(final AddClientReloadListenersEvent event) {
        SpriteSourceManager.registerDetails();
    }

    private static void onClientTick(final ClientTickEvent.Pre event) {
        AVClient.onClientTickStart(Minecraft.getInstance());
    }
}
