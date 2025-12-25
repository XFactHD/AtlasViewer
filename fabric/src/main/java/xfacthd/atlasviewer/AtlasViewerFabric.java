package xfacthd.atlasviewer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.KeyMapping;
import xfacthd.atlasviewer.client.AVClient;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;

@SuppressWarnings("unused") // Referenced from fabric.mod.json
public final class AtlasViewerFabric implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        AVClient.onRegisterKeyMappings(KeyMapping.Category.SORT_ORDER::add, KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.START_CLIENT_TICK.register(AVClient::onClientTickStart);
        ScreenEvents.AFTER_INIT.register((_, screen, _, _) ->
                ScreenStacker.onScreenInit(screen)
        );
    }
}
