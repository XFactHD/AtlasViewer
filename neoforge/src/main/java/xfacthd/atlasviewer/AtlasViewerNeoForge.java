package xfacthd.atlasviewer;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.NeoForge;
import xfacthd.atlasviewer.client.AVClient;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mod(AtlasViewer.MOD_ID)
public final class AtlasViewerNeoForge
{
    public AtlasViewerNeoForge() { }



    @EventBusSubscriber(modid = AtlasViewer.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class AVClientNeoForge
    {
        @SubscribeEvent
        public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
        {
            AVClient.onRegisterKeyMappings(event::register);

            NeoForge.EVENT_BUS.addListener(AVClientNeoForge::onClientTick);
        }

        @SubscribeEvent
        public static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event)
        {
            SpriteSourceManager.registerDetails();
        }

        private static void onClientTick(final ClientTickEvent.Pre event)
        {
            AVClient.onClientTickStart(Minecraft.getInstance());
        }



        private AVClientNeoForge() { }
    }
}
