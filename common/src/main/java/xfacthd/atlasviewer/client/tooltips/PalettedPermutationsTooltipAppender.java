package xfacthd.atlasviewer.client.tooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.network.chat.Component;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;
import xfacthd.atlasviewer.client.mixin.spritesources.AccessorPalettedPermutations;

public final class PalettedPermutationsTooltipAppender implements SourceTooltipAppender<PalettedPermutations>
{
    private static final Component LABEL_TEXTURES = Component.translatable(
            "label.atlasviewer.source_tooltip.palette.textures"
    );
    private static final Component LABEL_PERMUTTATIONS = Component.translatable(
            "label.atlasviewer.source_tooltip.palette.permutations"
    );
    private static final Component LABEL_PALETTE_KEY = Component.translatable(
            "label.atlasviewer.source_tooltip.palette.palette_key"
    );

    @Override
    public void accept(PalettedPermutations source, LineConsumer lineConsumer)
    {
        lineConsumer.accept(LABEL_TEXTURES, Component.empty());
        ((AccessorPalettedPermutations) source).atlasviewer$getTextures().forEach(tex -> lineConsumer.accept(
                null, Component.literal("  - ").append(Component.literal(tex.toString()))
        ));

        lineConsumer.accept(LABEL_PERMUTTATIONS, Component.empty());
        ((AccessorPalettedPermutations) source).atlasviewer$getPermutations().forEach((id, sprite) -> lineConsumer.accept(
                null, Component.literal("  - ")
                        .append(Component.literal(id).withStyle(ChatFormatting.ITALIC))
                        .append(": ")
                        .append(Component.literal(sprite.toString()))
        ));

        lineConsumer.accept(
                LABEL_PALETTE_KEY,
                Component.literal(((AccessorPalettedPermutations) source).atlasviewer$getPaletteKey().toString())
        );
    }
}
