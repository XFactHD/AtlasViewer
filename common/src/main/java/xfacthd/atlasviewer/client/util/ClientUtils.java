package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import xfacthd.atlasviewer.client.screen.state.FloatBlitRenderState;
import xfacthd.atlasviewer.client.screen.state.FloatColoredRectangleRenderState;
import xfacthd.atlasviewer.platform.Services;

import java.nio.ByteBuffer;
import java.util.Locale;
import java.util.function.Consumer;

public final class ClientUtils {
    public static final int MAX_MIP_LEVEL = 4;

    @Nullable
    private static Boolean arbClearTextureSupported = null;

    public static boolean isArbClearTextureSupported() {
        if (arbClearTextureSupported == null) {
            // Certain Intel iGPUs appear to have issues with GL_ARB_clear_texture
            String renderer = RenderSystem.getDevice().getDeviceInfo().name().toLowerCase(Locale.ROOT);
            if (!renderer.contains("intel")) {
                GLCapabilities capabilities = GL.getCapabilities();
                arbClearTextureSupported = capabilities.GL_ARB_clear_texture;
            } else {
                arbClearTextureSupported = false;
            }
        }
        return arbClearTextureSupported;
    }

    public static int getWrappedHeight(Font font, FormattedText text, int width) {
        return font.split(text, width).size() * font.lineHeight;
    }

    public static void drawColoredBox(GuiGraphicsExtractor graphics, int x, int y, int w, int h, int color) {
        graphics.fill(RenderPipelines.GUI, x,         y,         x + 1, y + h, color);
        graphics.fill(RenderPipelines.GUI, x + w - 1, y,         x + w, y + h, color);
        graphics.fill(RenderPipelines.GUI, x,         y,         x + w, y + 1, color);
        graphics.fill(RenderPipelines.GUI, x,         y + h - 1, x + w, y + h, color);
    }

    public static void drawColoredBox(GuiGraphicsExtractor graphics, float x, float y, float w, float h, int color) {
        fill(graphics, RenderPipelines.GUI, x,          y,          x + 1F, y + h,  color);
        fill(graphics, RenderPipelines.GUI, x + w - 1F, y,          x + w,  y + h,  color);
        fill(graphics, RenderPipelines.GUI, x,          y,          x + w,  y + 1F, color);
        fill(graphics, RenderPipelines.GUI, x,          y + h - 1F, x + w,  y + h,  color);
    }

    public static void fill(GuiGraphicsExtractor graphics, RenderPipeline pipeline, float minX, float minY, float maxX, float maxY, int color) {
        fill(graphics, pipeline, minX, minY, maxX, maxY, color, color);
    }

    public static void fill(GuiGraphicsExtractor graphics, RenderPipeline pipeline, float minX, float minY, float maxX, float maxY, int colorOne, int colorTwo) {
        Matrix3x2f pose = new Matrix3x2f(graphics.pose());
        ScreenRectangle scissorRect = Services.PLATFORM.peekScissorState(graphics);
        ScreenRectangle bounds = getBounds(minX, minY, maxX, maxY, pose, scissorRect);
        Services.PLATFORM.submitCustomGuiRenderState(graphics, new FloatColoredRectangleRenderState(
                pipeline, pose, minX, minY, maxX, maxY, colorOne, colorTwo, scissorRect, bounds
        ));
    }

    public static void blitSpecial(
            GuiGraphicsExtractor graphics,
            RenderPipeline pipeline,
            TextureSetup textureSetup,
            float minX,
            float minY,
            float maxX,
            float maxY,
            float minU,
            float maxU,
            float minV,
            float maxV,
            int color
    ) {
        Matrix3x2f pose = new Matrix3x2f(graphics.pose());
        ScreenRectangle scissorRect = Services.PLATFORM.peekScissorState(graphics);
        ScreenRectangle bounds = getBounds(minX, minY, maxX, maxY, pose, scissorRect);
        Services.PLATFORM.submitCustomGuiRenderState(graphics, new FloatBlitRenderState(
                pipeline, textureSetup, pose, minX, minY, maxX, maxY, minU, maxU, minV, maxV, color, scissorRect, bounds
        ));
    }

    public static @Nullable ScreenRectangle getBounds(float x0, float y0, float x1, float y1, Matrix3x2f pose, @Nullable ScreenRectangle scissorRect) {
        int x0i = Mth.floor(x0);
        int y0i = Mth.floor(y0);
        int x1i = Mth.ceil(x1);
        int y1i = Mth.ceil(y1);
        ScreenRectangle rect = new ScreenRectangle(x0i, y0i, x1i - x0i, y1i - y0i).transformMaxBounds(pose);
        return scissorRect != null ? scissorRect.intersection(rect) : rect;
    }

    public static void downloadTexture(GpuTexture srcTexture, int mipLevel, Consumer<NativeImage> imageConsumer) {
        GpuDevice device = RenderSystem.getDevice();
        CommandEncoder cmdEncoder = device.createCommandEncoder();

        int width = srcTexture.getWidth(mipLevel);
        int height = srcTexture.getHeight(mipLevel);
        int pixSize = srcTexture.getFormat().blockSize();
        int bufSize = width * height * pixSize;
        GpuBuffer buffer = device.createBuffer(() -> "Texture output buffer", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_READ, bufSize);
        cmdEncoder.copyTextureToBuffer(srcTexture, buffer, 0, () -> {
            try (GpuBufferSlice.MappedView bufView = buffer.map(true, false); NativeImage destImage = new NativeImage(width, height, false)) {
                ByteBuffer data = bufView.data();
                for (int y = 0; y < height; y++) {
                    for (int x = 0; x < width; x++) {
                        int pixel = data.getInt((x + y * width) * pixSize);
                        destImage.setPixelABGR(x, y, pixel);
                    }
                }
                imageConsumer.accept(destImage);
            }
            buffer.close();
        }, mipLevel);
    }

    public static int getMaxMipLevel(SpriteContents contents) {
        int lowestOneWidth = Integer.lowestOneBit(contents.width());
        int lowestOneHeight = Integer.lowestOneBit(contents.height());
        int lowestOne = Math.min(lowestOneWidth, lowestOneHeight);
        return Math.min(Mth.log2(lowestOne), MAX_MIP_LEVEL);
    }

    private ClientUtils() { }
}
