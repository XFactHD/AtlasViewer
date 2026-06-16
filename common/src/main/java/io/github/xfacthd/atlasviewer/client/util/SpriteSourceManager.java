package io.github.xfacthd.atlasviewer.client.util;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.atlasviewer.client.AVClient;
import io.github.xfacthd.atlasviewer.client.api.ISpriteSourcePackAwareLoader;
import io.github.xfacthd.atlasviewer.client.api.SourceAwareness;
import io.github.xfacthd.atlasviewer.client.api.SourceTooltipAppender;
import io.github.xfacthd.atlasviewer.client.mixin.AccessorSpriteSources;
import io.github.xfacthd.atlasviewer.platform.Services;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public final class SpriteSourceManager {
    private static final Component LABEL_FULL_TYPE = Component.translatable("label.atlasviewer.source_tooltip.full_type");
    private static final Component LABEL_REG_NAME = Component.translatable("label.atlasviewer.source_tooltip.reg_name");
    private static final Component VALUE_UNREGISTERED = Component.translatable("value.atlasviewer.source_tooltip.unregistered").withStyle(s -> s.withColor(0xD00000));
    private static final Map<Class<? extends SpriteSource.DiscardableLoader>, Function<SpriteSource.DiscardableLoader, Resource>> PRIMARY_RESOURCE_GETTERS = new IdentityHashMap<>();
    private static final Map<Class<? extends SpriteSource>, Function<SpriteSource, String>> SOURCE_STRINGIFIERS = new IdentityHashMap<>();
    private static final Map<Class<?>, String> SPECIAL_SOURCE_DESCRIPTIONS = new IdentityHashMap<>();
    private static final Map<Class<? extends SpriteSource>, SourceTooltipAppender<SpriteSource>> SOURCE_TOOLTIP_APPENDERS = new IdentityHashMap<>();
    private static boolean locked = true;

    @SuppressWarnings("unchecked")
    public static <T extends SpriteSource.DiscardableLoader> void registerPrimaryResourceGetter(
            Class<T> supplierType, Function<T, Resource> resourceGetter
    ) {
        Preconditions.checkState(!locked, "Registration is locked");
        if (PRIMARY_RESOURCE_GETTERS.put(supplierType, (Function<SpriteSource.DiscardableLoader, Resource>) resourceGetter) != null) {
            throw new IllegalStateException("Supplier type '%s' had a previous mapping".formatted(supplierType));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends SpriteSource> void registerSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    ) {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SOURCE_STRINGIFIERS.put(sourceType, (Function<SpriteSource, String>) stringifier) != null) {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    public static <T extends SpriteSource> void registerSimpleSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    ) {
        registerSourceStringifier(sourceType, src ->
                "'%s' ('%s')".formatted(src.getClass().getSimpleName(), stringifier.apply(src))
        );
    }

    public static <T extends SpriteSource> void registerSpecialSourceDescription(
            Class<T> sourceType, String description
    ) {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SPECIAL_SOURCE_DESCRIPTIONS.put(sourceType, description) != null) {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends SpriteSource> void registerSourceTooltipAppender(
            Class<T> sourceType, SourceTooltipAppender<T> appender
    ) {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SOURCE_TOOLTIP_APPENDERS.put(sourceType, (SourceTooltipAppender<SpriteSource>) appender) != null) {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    public static void copySpriteSupplierMetaToSpriteContents(SpriteSource.Loader function, @Nullable SpriteContents contents) {
        // SpriteSource.SpriteSupplier#apply() may return null if the processing fails
        if (contents == null) {
            return;
        }

        if (!(function instanceof SpriteSource.DiscardableLoader supplier)) {
            contents.atlasviewer$setSpriteSourceSourcePack(null, null, SourceAwareness.SPRITESUPPLIER_UNSUPPORTED, null, null);
            return;
        }

        SpriteSource.DiscardableLoader unwrappedSupplier = WrappedDiscardableLoader.resolve(supplier);
        Function<SpriteSource.DiscardableLoader, Resource> resourceGetter = PRIMARY_RESOURCE_GETTERS.get(unwrappedSupplier.getClass());
        if (resourceGetter != null) {
            contents.atlasviewer$captureMetaFromSpriteSupplier(supplier, resourceGetter.apply(unwrappedSupplier));
        } else if (!(unwrappedSupplier instanceof ISpriteSourcePackAwareLoader)) {
            contents.atlasviewer$setSpriteSourceSourcePack(null, null, SourceAwareness.SPRITESUPPLIER_UNSUPPORTED, null, null);
        }
    }

    public static String stringifySpriteSource(SpriteSource source) {
        source = WrappedSpriteSource.resolve(source);
        return SOURCE_STRINGIFIERS.getOrDefault(source.getClass(), SpriteSource::toString).apply(source);
    }

    public static @Nullable String getSpecialDescription(Class<?> sourceType) {
        return SPECIAL_SOURCE_DESCRIPTIONS.get(sourceType);
    }

    public static List<SourceTooltipLine> buildSourceTooltip(SpriteSource source, String typeName) {
        List<SourceTooltipLine> lines = new ArrayList<>();
        source = WrappedSpriteSource.resolve(source);

        Identifier regLoc = AccessorSpriteSources.atlasviewer$getTypes().atlasviewer$getKey(source.codec());
        Component regName = regLoc != null ? Component.literal(regLoc.toString()) : VALUE_UNREGISTERED;
        lines.add(new SourceTooltipLine(LABEL_REG_NAME, regName));

        SourceTooltipAppender<SpriteSource> appender = SOURCE_TOOLTIP_APPENDERS.get(source.getClass());
        if (appender != null) {
            appender.accept(source, (title, content) -> lines.add(new SourceTooltipLine(title, content)));
        }

        lines.add(new SourceTooltipLine(LABEL_FULL_TYPE, Component.literal(typeName)));

        return lines;
    }

    public static void registerDetails() {
        locked = false;
        AVClient.registerBuiltInSpriteSourceDetails();
        Services.PLATFORM.registerPlatformSpecificBuiltInSpriteSourceDetails();
        Services.PLATFORM.fireSpriteSourceDetailsEvent();
        locked = true;
    }

    private SpriteSourceManager() { }
}
