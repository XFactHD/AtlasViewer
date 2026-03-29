package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;

public final class SingleFileTooltipAppender implements SourceTooltipAppender<SingleFile> {
    private static final Component LABEL_RESOURCE_ID = Component.translatable(
            "label.atlasviewer.source_tooltip.single_file.resource"
    );
    private static final Component LABEL_SPRITE_ID = Component.translatable(
            "label.atlasviewer.source_tooltip.single_file.sprite"
    );

    @Override
    public void accept(SingleFile source, LineConsumer lineConsumer) {
        Identifier resourceId = source.resourceId();
        Identifier spriteId = source.spriteId().orElse(resourceId);

        lineConsumer.accept(LABEL_RESOURCE_ID, Component.literal(resourceId.toString()));
        lineConsumer.accept(LABEL_SPRITE_ID, Component.literal(spriteId.toString()));
    }
}
