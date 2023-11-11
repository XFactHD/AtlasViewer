package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * Event for the registration of additional details of custom sprite sources.
 * <p>
 * Fired on the mod event bus from client-side reload listener registration
 */
public final class RegisterSpriteSourceDetailsEvent extends Event implements IModBusEvent
{
    private final StringifierRegistrar stringifierRegistrar;
    private final StringifierRegistrar simpleStringifierRegistrar;
    private final DescriptionRegistrar descriptionRegistrar;
    private final TooltipAppenderRegistrar tooltipAppenderRegistrar;

    @ApiStatus.Internal
    public RegisterSpriteSourceDetailsEvent(
            StringifierRegistrar stringifierRegistrar,
            StringifierRegistrar simpleStringifierRegistrar,
            DescriptionRegistrar descriptionRegistrar,
            TooltipAppenderRegistrar tooltipAppenderRegistrar
    )
    {
        this.stringifierRegistrar = stringifierRegistrar;
        this.simpleStringifierRegistrar = simpleStringifierRegistrar;
        this.descriptionRegistrar = descriptionRegistrar;
        this.tooltipAppenderRegistrar = tooltipAppenderRegistrar;
    }

    /**
     * Register a simple stringifier printing the {@link SpriteSource}'s simple class name and the string
     * returned by the provided stringifier function
     */
    public <T extends SpriteSource> void registerSimpleSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    )
    {
        simpleStringifierRegistrar.register(sourceType, stringifier);
    }

    /**
     * Register a fully custom stringifier for the given {@link SpriteSource} type.
     */
    public <T extends SpriteSource> void registerSourceStringifier(Class<T> sourceType, Function<T, String> stringifier)
    {
        stringifierRegistrar.register(sourceType, stringifier);
    }

    /**
     * Add a special description string to be display instead of the source type's simple class name in
     * the sprite details screen
     * @apiNote any given {@link SpriteSource} may only have either a special description (takes precedence) or a tooltip appender
     */
    public <T extends SpriteSource> void registerSpecialSourceDescription(Class<T> sourceType, String description)
    {
        descriptionRegistrar.register(sourceType, description);
    }

    /**
     * Register a tooltip appender for the given {@link SpriteSource} type to add additional info to the source
     * tooltip
     * @apiNote any given {@link SpriteSource} may only have either a special description (takes precedence) or a tooltip appender
     */
    public <T extends SpriteSource> void registerSourceTooltipAppender(
            Class<T> sourceType, SourceTooltipAppender<T> appender
    )
    {
        tooltipAppenderRegistrar.register(sourceType, appender);
    }



    @ApiStatus.Internal
    @FunctionalInterface
    public interface StringifierRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, Function<T, String> stringifer);
    }

    @ApiStatus.Internal
    @FunctionalInterface
    public interface DescriptionRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, String description);
    }

    @ApiStatus.Internal
    @FunctionalInterface
    public interface TooltipAppenderRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, SourceTooltipAppender<T> appender);
    }
}
