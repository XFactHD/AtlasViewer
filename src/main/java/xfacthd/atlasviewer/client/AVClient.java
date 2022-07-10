package xfacthd.atlasviewer.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.screen.AtlasScreen;

@Mod.EventBusSubscriber(modid = AtlasViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AVClient
{
    private static final Lazy<KeyMapping> KEY_MAPPING_OPEN_VIEWER = makeKeyMapping();

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(KEY_MAPPING_OPEN_VIEWER.get());

        MinecraftForge.EVENT_BUS.addListener(AVClient::onClientTick);
    }

    private static void onClientTick(final TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) { return; }
        if (Minecraft.getInstance().level == null || Minecraft.getInstance().player == null) { return; }
        if (Minecraft.getInstance().screen != null) { return; }

        if (KEY_MAPPING_OPEN_VIEWER.get().consumeClick())
        {
            Minecraft.getInstance().setScreen(new AtlasScreen());
        }
    }

    private static Lazy<KeyMapping> makeKeyMapping()
    {
        return Lazy.of(() ->
            new KeyMapping("key.atlasviewer.open_viewer", GLFW.GLFW_KEY_V, "key.categories.atlasviewer")
        );
    }
}
