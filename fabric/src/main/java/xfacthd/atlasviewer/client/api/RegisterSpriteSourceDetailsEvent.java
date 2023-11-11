package xfacthd.atlasviewer.client.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Event for the registration of additional details of custom sprite sources.
 */
public final class RegisterSpriteSourceDetailsEvent
{
    public static final Event<RegisterSpriteSourceDetails> EVENT = EventFactory.createArrayBacked(
            RegisterSpriteSourceDetails.class,
            callbacks -> (stringifierRegistrar, simpleStringifierRegistrar, descriptionRegistrar, appenderRegistrar) ->
                    Arrays.stream(callbacks).forEach(cb ->
                            cb.accept(stringifierRegistrar, simpleStringifierRegistrar, descriptionRegistrar, appenderRegistrar)
                    )
    );



    @FunctionalInterface
    public interface RegisterSpriteSourceDetails
    {
        /**
         * @param stringifierRegistrar Registrar for a fully custom stringifier for the given {@link SpriteSource} type.
         * @param simpleStringifierRegistrar Registrar for a simple stringifier printing the {@link SpriteSource}'s
         *                                   simple class name and the string returned by the provided stringifier function
         * @param descriptionRegistrar Registrar for a special description string to be display instead of the source
         *                             type's simple class name in the sprite details screen
         * @param tooltipAppenderRegistrar Registrar for a tooltip appender to add
         */
        void accept(
                StringifierRegistrar stringifierRegistrar,
                StringifierRegistrar simpleStringifierRegistrar,
                DescriptionRegistrar descriptionRegistrar,
                TooltipAppenderRegistrar tooltipAppenderRegistrar
        );
    }

    @FunctionalInterface
    public interface StringifierRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, Function<T, String> stringifer);
    }

    @FunctionalInterface
    public interface DescriptionRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, String description);
    }

    @FunctionalInterface
    public interface TooltipAppenderRegistrar
    {
        <T extends SpriteSource> void register(Class<T> sourceType, SourceTooltipAppender<T> appender);
    }



    private RegisterSpriteSourceDetailsEvent() { }
}
