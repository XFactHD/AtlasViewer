package xfacthd.atlasviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import xfacthd.atlasviewer.client.AVClient;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;

public final class AtlasViewerFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        AVClient.onRegisterKeyMappings(KeyBindingHelper::registerKeyBinding);
        ClientTickEvents.START_CLIENT_TICK.register(AVClient::onClientTickStart);
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) ->
                ScreenStacker.onScreenInit(screen)
        );
    }
}
