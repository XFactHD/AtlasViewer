package xfacthd.atlasviewer.client.util;

import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraftforge.fml.ModLoader;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public final class SpriteSourceManager
{
    private static final Map<Class<? extends SpriteSource>, Function<SpriteSource, String>> SOURCE_STRINGIFIERS = new IdentityHashMap<>();
    private static final Map<Class<?>, String> SPECIAL_SOURCE_DESCRIPTIONS = new IdentityHashMap<>();
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

    public static void registerSpecialSourceDescription(Class<?> sourceType, String description)
    {
        Preconditions.checkState(!locked, "Registration is locked");
        if (SPECIAL_SOURCE_DESCRIPTIONS.put(sourceType, description) != null)
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

    public static void registerDetails()
    {
        locked = false;
        ModLoader.get().postEvent(new RegisterSpriteSourceDetailsEvent(
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription
        ));
        locked = true;
    }



    private SpriteSourceManager() { }
}
