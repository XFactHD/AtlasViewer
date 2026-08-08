package io.github.xfacthd.atlasviewer.client.sourcehandling;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public final class SourceTooltipLine {
    @Nullable
    private Component label;
    private Component content;

    public SourceTooltipLine(@Nullable Component label, Component content) {
        this.label = label;
        this.content = content;
    }

    public @Nullable Component getLabel() {
        return label;
    }

    public Component getContent() {
        return content;
    }

    public void setLabel(@Nullable Component label) {
        this.label = label;
    }

    public void setContent(Component content) {
        this.content = content;
    }
}
