package io.github.xfacthd.atlasviewer.client.sourcehandling;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.Nullable;
import io.github.xfacthd.atlasviewer.client.api.IPackAwareSpriteSource;
import io.github.xfacthd.atlasviewer.client.api.SpriteSourceMeta;
import io.github.xfacthd.atlasviewer.platform.Services;

public class WrappedSpriteSource implements SpriteSource, IPackAwareSpriteSource {
    protected final SpriteSource wrapped;
    private final SpriteSourceMeta meta = new SpriteSourceMeta();

    public WrappedSpriteSource(SpriteSource wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public void run(ResourceManager resourceManager, Output output) {
        wrapped.run(resourceManager, output);
    }

    @Override
    public MapCodec<? extends SpriteSource> codec() {
        return wrapped.codec();
    }

    @Override
    public SpriteSourceMeta atlasviewer$getMeta() {
        return meta;
    }

    public static SpriteSource of(SpriteSource wrapped) {
        SpriteSourceMeta meta = wrapped.atlasviewer$getMeta();
        if (meta != SpriteSourceMeta.Unsupported.INSTANCE) {
            // Don't wrap if the sprite source already implements the API
            return wrapped;
        }
        return Services.PLATFORM.wrapSpriteSource(wrapped);
    }

    @Contract("!null->!null")
    public static @Nullable SpriteSource resolve(@Nullable SpriteSource source) {
        if (source instanceof WrappedSpriteSource wrapped) {
            return wrapped.wrapped;
        }
        return source;
    }
}
