package xfacthd.atlasviewer;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import xfacthd.atlasviewer.client.AVClient;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mod(AtlasViewer.MOD_ID)
public final class AtlasViewerNeoForge
{
    public AtlasViewerNeoForge() { }



    @Mod.EventBusSubscriber(modid = AtlasViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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

        private static void onClientTick(final TickEvent.ClientTickEvent event)
        {
            if (event.phase == TickEvent.Phase.START)
            {
                AVClient.onClientTickStart(Minecraft.getInstance());
            }
        }



        private AVClientNeoForge() { }
    }
}
