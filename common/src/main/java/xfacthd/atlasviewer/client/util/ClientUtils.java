package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.FormattedText;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class ClientUtils
{
    @Nullable
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

    public static void downloadTexture(GpuTexture srcTexture, int mipLevel, Consumer<NativeImage> imageConsumer)
    {
        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder cmdEncoder = device.createCommandEncoder();

        int width = srcTexture.getWidth(mipLevel);
        int height = srcTexture.getHeight(mipLevel);
        int pixSize = srcTexture.getFormat().pixelSize();
        int bufSize = width * height * pixSize;
        GpuBuffer buffer = device.createBuffer(() -> "Texture output buffer", BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, bufSize);
        cmdEncoder.copyTextureToBuffer(srcTexture, buffer, 0, () ->
        {
            try (GpuBuffer.ReadView bufView = cmdEncoder.readBuffer(buffer); NativeImage destImage = new NativeImage(width, height, false))
            {
                ByteBuffer data = bufView.data();
                for (int y = 0; y < height; y++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        int pixel = data.getInt((x + y * width) * pixSize);
                        destImage.setPixelABGR(x, y, pixel);
                    }
                }
                imageConsumer.accept(destImage);
            }
            buffer.close();
        }, mipLevel);
    }



    private ClientUtils() { }
}
