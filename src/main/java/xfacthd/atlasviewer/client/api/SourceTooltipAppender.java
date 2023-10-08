package xfacthd.atlasviewer.client.api;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface SourceTooltipAppender<T extends SpriteSource> extends BiConsumer<T, SourceTooltipAppender.LineConsumer>
{
    @Override
    void accept(T source, LineConsumer lineConsumer);



    @FunctionalInterface
    interface LineConsumer extends BiConsumer<Component, Component>
    {
        @Override
        void accept(@Nullable Component title, Component content);
    }
}
