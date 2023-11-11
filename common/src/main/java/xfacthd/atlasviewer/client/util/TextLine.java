package xfacthd.atlasviewer.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;

public record TextLine(Component text, Component fullText, boolean capped)
{
    public TextLine(Component text)
    {
        this(text, text, false);
    }

    public static TextLine of(String text, Font font, int maxWidth)
    {
        String cappedText = text;
        boolean capped = false;
        if (font.width(text) > maxWidth)
        {
            cappedText = font.plainSubstrByWidth(text, maxWidth) + "...";
            capped = true;
        }
        return new TextLine(Component.literal(cappedText), Component.literal(text), capped);
    }
}
