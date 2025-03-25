package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.client.renderer.texture.atlas.sources.Unstitcher;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;

public final class UnstitcherTooltipAppender implements SourceTooltipAppender<Unstitcher>
{
    private static final Component LABEL_RESOURCE = Component.translatable(
            "label.atlasviewer.source_tooltip.unstitcher.resource"
    );
    private static final Component LABEL_REGIONS = Component.translatable(
            "label.atlasviewer.source_tooltip.unstitcher.regions"
    );
    private static final String VALUE_REGION = "value.atlasviewer.source_tooltip.unstitcher.region";
    private static final Component LABEL_X_DIVISOR = Component.translatable(
            "label.atlasviewer.source_tooltip.unstitcher.x_divisor"
    );
    private static final Component LABEL_Y_DIVISOR = Component.translatable(
            "label.atlasviewer.source_tooltip.unstitcher.y_divisor"
    );

    @Override
    public void accept(Unstitcher source, LineConsumer lineConsumer)
    {
        lineConsumer.accept(LABEL_RESOURCE, Component.literal(source.resource().toString()));

        lineConsumer.accept(LABEL_REGIONS, Component.empty());
        source.regions().forEach(region -> lineConsumer.accept(null, Component.translatable(
                VALUE_REGION, region.sprite(), region.x(), region.y(), region.width(), region.height()
        )));

        lineConsumer.accept(LABEL_X_DIVISOR, Component.literal(Double.toString(source.xDivisor())));
        lineConsumer.accept(LABEL_Y_DIVISOR, Component.literal(Double.toString(source.yDivisor())));
    }
}
