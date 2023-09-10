package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
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
    private final DescriptionRegistrar descriptionRegistrar;

    @ApiStatus.Internal
    public RegisterSpriteSourceDetailsEvent(
            StringifierRegistrar stringifierRegistrar, DescriptionRegistrar descriptionRegistrar
    )
    {
        this.stringifierRegistrar = stringifierRegistrar;
        this.descriptionRegistrar = descriptionRegistrar;
    }

    /**
     * Register a simple stringifier printing the {@link SpriteSource}'s simple class name and the string
     * returned by the provided stringifier function
     */
    public <T extends SpriteSource> void registerSimpleSourceStringifier(
            Class<T> sourceType, Function<T, String> stringifier
    )
    {
        registerSourceStringifier(sourceType, src ->
                "'%s' ('%s')".formatted(src.getClass().getSimpleName(), stringifier.apply(src))
        );
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
     */
    public void registerSpecialSourceDescription(Class<?> sourceType, String description)
    {
        descriptionRegistrar.register(sourceType, description);
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
        void register(Class<?> sourceType, String description);
    }
}
