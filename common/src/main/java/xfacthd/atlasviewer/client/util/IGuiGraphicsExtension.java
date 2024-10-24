package xfacthd.atlasviewer.client.util;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface IGuiGraphicsExtension
{
    /**
     * {@link GuiGraphics#fill(RenderType, int, int, int, int, int, int)} with support for float coordinates
     */
    default void atlasviewer$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, float z, int color)
    {
        throw new AssertionError("Not injected");
    }

    /**
     * {@link GuiGraphics#innerBlit(Function, ResourceLocation, int, int, int, int, float, float, float, float, int)}
     * with support for float coordinates
     */
    default void atlasviewer$innerBlit(
            Function<ResourceLocation, RenderType> renderType,
            ResourceLocation texture,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float minU,
            float maxU,
            float minV,
            float maxV,
            int color
    )
    {
        throw new AssertionError("Not injected");
    }
}
