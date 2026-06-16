package io.github.xfacthd.atlasviewer.client;

import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;
import io.github.xfacthd.atlasviewer.client.tooltips.NamespacedDirectoryListerTooltipAppender;
import io.github.xfacthd.atlasviewer.client.util.SpriteSourceManager;

public final class AVClientNeoForge {
    public static void registerBuiltInSpriteSourceDetails() {
        SpriteSourceManager.registerSimpleSourceStringifier(
                NamespacedDirectoryLister.class,
                NamespacedDirectoryLister::sourcePath
        );

        SpriteSourceManager.registerSourceTooltipAppender(
                NamespacedDirectoryLister.class,
                new NamespacedDirectoryListerTooltipAppender()
        );
    }

    private AVClientNeoForge() { }
}
