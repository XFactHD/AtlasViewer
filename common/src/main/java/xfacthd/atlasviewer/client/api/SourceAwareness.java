package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceList;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Locale;

/**
 * Indicates to which degree the different stages of texture loading are aware of the source pack where the
 * sprite source loading a given texture came from
 */
public enum SourceAwareness
{
    /**
     * The source is known to all stages of the relevant loading path
     */
    SOURCE_KNOWN,
    /**
     * The {@link SpriteContents} did not receive any data.
     * This usually means that a custom {@link SpriteSource.SpriteSupplier} or {@link SpriteSource} which does
     * not implement the necessary APIs created these {@link SpriteContents}
     */
    SPRITECONTENTS_UNAWARE,
    /**
     * The {@link SpriteSource.SpriteSupplier} did not receive any data.
     * This usually means that a custom {@link SpriteSource} which does not implement the necessary
     * APIs created this {@link SpriteSource.SpriteSupplier}
     */
    SPRITESUPPLIER_UNAWARE,
    /**
     * The {@link Resource} did not receive any data.
     * This usually means that a custom {@link SpriteSource.SpriteSupplier} which does not implement the
     * necessary APIs touched this {@link Resource}
     */
    RESOURCE_UNAWARE,
    /**
     * The {@link SpriteSource} was forcefully inserted into the loading process (i.e. via mixin into
     * {@link SpriteSourceList#load(ResourceManager, ResourceLocation)}) and therefore does not have a source pack
     */
    SPRITESOURCE_FORCED,
    /**
     * The {@link SpriteSource} did not receive any data.
     * This may indicate that the {@link SpriteSource} was forcefully injected into the loading process
     * (i.e. via mixin into {@link SpriteSourceList#list(ResourceManager)} and therefore does not have a source pack
     */
    SPRITESOURCE_UNAWARE,
    /**
     * The {@link SpriteSource} does not implement the necessary APIs and therefore cannot keep track of its source pack
     */
    SPRITESOURCE_UNSUPPORTED;

    private final Component description = Component.translatable(
            "msg.atlasviewer.source_awareness." + toString().toLowerCase(Locale.ROOT)
    ).setStyle(ordinal() == 0 ? Style.EMPTY : Style.EMPTY.withColor(0xD00000));

    private final Component tooltip = Component.translatable(
            "tooltip.atlasviewer.source_awareness." + toString().toLowerCase(Locale.ROOT)
    ).setStyle(ordinal() == 0 ? Style.EMPTY : Style.EMPTY.withColor(0xD00000));

    public Component getDescription()
    {
        return description;
    }

    public Component getTooltip()
    {
        return tooltip;
    }
}
