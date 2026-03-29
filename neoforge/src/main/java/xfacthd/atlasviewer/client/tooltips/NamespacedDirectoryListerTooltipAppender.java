package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;

public final class NamespacedDirectoryListerTooltipAppender implements SourceTooltipAppender<NamespacedDirectoryLister> {
    private static final Component LABEL_SOURCE_PATH = Component.translatable(
            "label.atlasviewer.source_tooltip.dir_lister.path"
    );
    private static final Component LABEL_ID_PREFIX = Component.translatable(
            "label.atlasviewer.source_tooltip.dir_lister.prefix"
    );
    private static final Component LABEL_NAMESPACE = Component.translatable(
            "label.atlasviewer.source_tooltip.namespaced_dir_lister.namespace"
    );

    @Override
    public void accept(NamespacedDirectoryLister source, LineConsumer lineConsumer) {
        lineConsumer.accept(LABEL_SOURCE_PATH, Component.literal(source.sourcePath()));
        lineConsumer.accept(LABEL_ID_PREFIX, Component.literal(source.idPrefix()));
        lineConsumer.accept(LABEL_NAMESPACE, Component.literal(source.namespace()));
    }
}
