package xfacthd.atlasviewer.client;

import com.google.common.base.Suppliers;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.atlas.sources.*;
import org.lwjgl.glfw.GLFW;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorDirectoryLister;
import xfacthd.atlasviewer.client.screen.AtlasScreen;
import xfacthd.atlasviewer.client.tooltips.*;
import xfacthd.atlasviewer.client.util.MissingTextureDummySpriteSource;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

import java.util.function.Consumer;
import java.util.function.Supplier;

public final class AVClient
{
    private static final Supplier<KeyMapping> KEY_MAPPING_OPEN_VIEWER = Suppliers.memoize(() ->
            new KeyMapping("key.atlasviewer.open_viewer", GLFW.GLFW_KEY_V, "key.categories.atlasviewer")
    );

    public static void onRegisterKeyMappings(Consumer<KeyMapping> registrar)
    {
        registrar.accept(KEY_MAPPING_OPEN_VIEWER.get());
    }

    public static void registerBuiltInSpriteSourceDetails()
    {
        SpriteSourceManager.registerPrimaryResourceGetter(
                Unstitcher.RegionInstance.class,
                region -> region.atlasviewer$getImage().atlasviewer$getResource()
        );
        SpriteSourceManager.registerPrimaryResourceGetter(
                PalettedPermutations.PalettedSpriteSupplier.class,
                supplier -> supplier.baseImage().atlasviewer$getResource()
        );

        SpriteSourceManager.registerSimpleSourceStringifier(
                DirectoryLister.class,
                AccessorDirectoryLister::atlasviewer$getSourcePath
        );
        SpriteSourceManager.registerSimpleSourceStringifier(
                SingleFile.class,
                file -> file.atlasviewer$getResourceId().toString()
        );
        SpriteSourceManager.registerSimpleSourceStringifier(
                PalettedPermutations.class,
                permutations -> permutations.atlasviewer$getTextures().toString()
        );
        SpriteSourceManager.registerSimpleSourceStringifier(
                Unstitcher.class,
                unstitcher -> unstitcher.atlasviewer$getResource().toString()
        );

        SpriteSourceManager.registerSpecialSourceDescription(
                MissingTextureDummySpriteSource.class,
                "builtin (synthetic)"
        );

        SpriteSourceManager.registerSourceTooltipAppender(
                DirectoryLister.class,
                new DirectoryListerTooltipAppender()
        );
        SpriteSourceManager.registerSourceTooltipAppender(
                SingleFile.class,
                new SingleFileTooltipAppender()
        );
        SpriteSourceManager.registerSourceTooltipAppender(
                PalettedPermutations.class,
                new PalettedPermutationsTooltipAppender()
        );
        SpriteSourceManager.registerSourceTooltipAppender(
                Unstitcher.class,
                new UnstitcherTooltipAppender()
        );
    }

    public static void onClientTickStart(Minecraft mc)
    {
        if (mc.level == null || mc.player == null || mc.screen != null)
        {
            return;
        }

        if (KEY_MAPPING_OPEN_VIEWER.get().consumeClick())
        {
            Minecraft.getInstance().setScreen(new AtlasScreen());
        }
    }



    private AVClient() { }
}
