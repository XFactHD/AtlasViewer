package xfacthd.atlasviewer.client.util;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import xfacthd.atlasviewer.client.AVClient;
import xfacthd.atlasviewer.client.api.SourceTooltipAppender;
import xfacthd.atlasviewer.platform.Services;

import java.util.*;
import java.util.function.Function;

public final class SpriteSourceManager
{
    private static final Component LABEL_FULL_TYPE = Component.translatable("label.atlasviewer.source_tooltip.full_type");
    private static final Component LABEL_REG_NAME = Component.translatable("label.atlasviewer.source_tooltip.reg_name");
    private static final Component VALUE_UNREGISTERED = Component.translatable("value.atlasviewer.source_tooltip.unregistered").withStyle(s -> s.withColor(0xD00000));
    private static final Map<Class<? extends SpriteSource>, Function<SpriteSource, String>> SOURCE_STRINGIFIERS = new IdentityHashMap<>();
    private static final Map<Class<?>, String> SPECIAL_SOURCE_DESCRIPTIONS = new IdentityHashMap<>();
    private static final Map<Class<? extends SpriteSource>, SourceTooltipAppender<SpriteSource>> SOURCE_TOOLTIP_APPENDERS = new IdentityHashMap<>();
    private static boolean locked = true;

    @SuppressWarnings("unchecked")
    public static <T extends SpriteSource> void registerSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    )
    {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SOURCE_STRINGIFIERS.put(sourceType, (Function<SpriteSource, String>) stringifier) != null)
        {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    public static <T extends SpriteSource> void registerSimpleSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    )
    {
        registerSourceStringifier(sourceType, src ->
                "'%s' ('%s')".formatted(src.getClass().getSimpleName(), stringifier.apply(src))
        );
    }

    public static <T extends SpriteSource> void registerSpecialSourceDescription(
            Class<T> sourceType, String description
    )
    {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SPECIAL_SOURCE_DESCRIPTIONS.put(sourceType, description) != null)
        {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends SpriteSource> void registerSourceTooltipAppender(
            Class<T> sourceType, SourceTooltipAppender<T> appender
    )
    {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SOURCE_TOOLTIP_APPENDERS.put(sourceType, (SourceTooltipAppender<SpriteSource>) appender) != null)
        {
            throw new IllegalStateException("Source type '%s' had a previous mapping".formatted(sourceType));
        }
    }

    public static String stringifySpriteSource(SpriteSource source)
    {
        return SOURCE_STRINGIFIERS.getOrDefault(source.getClass(), SpriteSource::toString).apply(source);
    }

    public static String getSpecialDescription(Class<?> sourceType)
    {
        return SPECIAL_SOURCE_DESCRIPTIONS.get(sourceType);
    }

    public static List<Tuple<Component, Component>> buildSourceTooltip(SpriteSource source, String typeName)
    {
        List<Tuple<Component, Component>> lines = new ArrayList<>();

        ResourceLocation regLoc = SpriteSources.TYPES.inverse().get(source.type());
        Component regName = regLoc != null ? Component.literal(regLoc.toString()) : VALUE_UNREGISTERED;
        lines.add(new Tuple<>(LABEL_REG_NAME, regName));

        SourceTooltipAppender<SpriteSource> appender = SOURCE_TOOLTIP_APPENDERS.get(source.getClass());
        if (appender != null)
        {
            //noinspection ConstantConditions
            appender.accept(source, (title, content) -> lines.add(new Tuple<>(title, content)));
        }

        lines.add(new Tuple<>(LABEL_FULL_TYPE, Component.literal(typeName)));

        return lines;
    }

    public static void registerDetails()
    {
        locked = false;
        AVClient.registerBuiltInSpriteSourceDetails();
        Services.PLATFORM.fireSpriteSourceDetailsEvent();
        locked = true;
    }



    private SpriteSourceManager() { }
}
