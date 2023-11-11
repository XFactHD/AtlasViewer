package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.client.renderer.texture.atlas.sources.DirectoryLister;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorDirectoryLister;

public final class DirectoryListerTooltipAppender implements SourceTooltipAppender<DirectoryLister>
{
    private static final Component LABEL_SOURCE_PATH = Component.translatable(
            "label.atlasviewer.source_tooltip.dir_lister.path"
    );
    private static final Component LABEL_ID_PREFIX = Component.translatable(
            "label.atlasviewer.source_tooltip.dir_lister.prefix"
    );

    @Override
    public void accept(DirectoryLister source, LineConsumer lineConsumer)
    {
        lineConsumer.accept(
                LABEL_SOURCE_PATH,
                Component.literal(((AccessorDirectoryLister) source).atlasviewer$getSourcePath())
        );

        lineConsumer.accept(
                LABEL_ID_PREFIX,
                Component.literal(((AccessorDirectoryLister) source).atlasviewer$getIdPrefix())
        );
    }
}
