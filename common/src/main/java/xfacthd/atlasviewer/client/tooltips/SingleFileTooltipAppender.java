package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorSingleFile;

public final class SingleFileTooltipAppender implements SourceTooltipAppender<SingleFile>
{
    private static final Component LABEL_RESOURCE_ID = Component.translatable(
            "label.atlasviewer.source_tooltip.single_file.resource"
    );
    private static final Component LABEL_SPRITE_ID = Component.translatable(
            "label.atlasviewer.source_tooltip.single_file.sprite"
    );

    @Override
    public void accept(SingleFile source, LineConsumer lineConsumer)
    {
        ResourceLocation resourceId = ((AccessorSingleFile) source).atlasviewer$getResourceId();
        ResourceLocation spriteId = ((AccessorSingleFile) source).atlasviewer$getSpriteId().orElse(resourceId);

        lineConsumer.accept(LABEL_RESOURCE_ID, Component.literal(resourceId.toString()));
        lineConsumer.accept(LABEL_SPRITE_ID, Component.literal(spriteId.toString()));
    }
}
