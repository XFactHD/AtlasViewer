package xfacthd.atlasviewer.client.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FormattedText;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

public final class ClientUtils
{
    private static Boolean arbClearTextureSupported = null;

    public static boolean isArbClearTextureSupported()
    {
        if (arbClearTextureSupported == null)
        {
            GLCapabilities capabilities = GL.getCapabilities();
            arbClearTextureSupported = capabilities.GL_ARB_clear_texture;
        }
        return arbClearTextureSupported;
    }

    public static int getWrappedHeight(Font font, FormattedText text, int width)
    {
        return font.split(text, width).size() * font.lineHeight;
    }

    public static void drawColoredBox(GuiGraphics graphics, float x, float y, float w, float h, int color)
    {
        graphics.atlasviewer$fill(RenderType.guiOverlay(), x,          y,          x + 1F, y + h,  0, color);
        graphics.atlasviewer$fill(RenderType.guiOverlay(), x + w - 1F, y,          x + w,  y + h,  0, color);
        graphics.atlasviewer$fill(RenderType.guiOverlay(), x,          y,          x + w,  y + 1F, 0, color);
        graphics.atlasviewer$fill(RenderType.guiOverlay(), x,          y + h - 1F, x + w,  y + h,  0, color);
    }



    private ClientUtils() { }
}
