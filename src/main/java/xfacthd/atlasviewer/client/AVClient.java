package xfacthd.atlasviewer.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.atlas.sources.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.mixin.spritesources.*;
import xfacthd.atlasviewer.client.screen.AtlasScreen;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mod.EventBusSubscriber(modid = AtlasViewer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class AVClient
{
    private static final Lazy<KeyMapping> KEY_MAPPING_OPEN_VIEWER = makeKeyMapping();

    @SubscribeEvent
    public static void onRegisterKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(KEY_MAPPING_OPEN_VIEWER.get());

        MinecraftForge.EVENT_BUS.addListener(AVClient::onClientTick);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(final RegisterClientReloadListenersEvent event)
    {
        SpriteSourceManager.registerDetails();
    }

    @SubscribeEvent
    public static void onRegisterSpriteSourceDetails(final RegisterSpriteSourceDetailsEvent event)
    {
        event.registerSimpleSourceStringifier(
                DirectoryLister.class,
                lister -> ((AccessorDirectoryLister) lister).atlasviewer$getSourcePath()
        );
        event.registerSimpleSourceStringifier(
                SingleFile.class,
                file -> ((AccessorSingleFile) file).atlasviewer$getResourceId().toString()
        );
        event.registerSimpleSourceStringifier(
                PalettedPermutations.class,
                permutations -> ((AccessorPalettedPermutations) permutations).atlasviewer$getTextures().toString()
        );
        event.registerSimpleSourceStringifier(
                Unstitcher.class,
                unstitcher -> ((AccessorUnstitcher) unstitcher).atlasviewer$getResource().toString()
        );

        event.registerSpecialSourceDescription(
                MissingTextureAtlasSprite.class,
                "builtin (synthetic)"
        );
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



    private AVClient() { }
}
