package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.GameRenderer;

@SuppressWarnings("unused")
public class TextureDrawer
{
    private static BufferBuilder buffer;

    /**
     * Draw a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(PoseStack pstack, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(pstack, x, y, 0, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawTexture(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a tinted texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawTexture(PoseStack pstack, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startTinted();
        fillBuffer(pstack, x, y, 0, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    /**
     * Draw a texture with a size of 256x256 in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     */
    public static void drawGuiTexture(PoseStack pstack, float x, float y, float z, float texX, float texY, float w, float h)
    {
        start();
        fillGuiBuffer(pstack, x, y, z, texX, texY, w, h);
        end();
    }

    /**
     * Draw a tinted texture with a size of 256x256 in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawGuiTexture(PoseStack pstack, float x, float y, float z, float texX, float texY, float w, float h, int color)
    {
        startTinted();
        fillGuiBuffer(pstack, x, y, z, texX, texY, w, h, color);
        end();
    }

    /**
     * Draw a texture with arbitrary dimensions in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void drawGuiTexture(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        start();
        fillGuiBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV);
        end();
    }

    /**
     * Draw a tinted texture with arbitrary dimensions in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void drawGuiTexture(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        startTinted();
        fillGuiBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV, color);
        end();
    }

    /**
     * Draw a colored rectangle with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param color Color to tint the texture in
     */
    public static void drawColor(PoseStack pstack, float x, float y, float z, float w, float h, int color)
    {
        startColored();
        fillColorBuffer(pstack, x, y, z, w, h, color);
        end();
    }

    /**
     * Draw a colored rectangle with arbitrary dimensions in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param color Color to tint the texture in
     */
    public static void drawGuiColor(PoseStack pstack, float x, float y, float z, float w, float h, int color)
    {
        startColored();
        fillGuiColorBuffer(pstack, x, y, z, w, h, color);
        end();
    }

    /**
     * Start drawing textures to a buffer<br>
     * Call before using {@link TextureDrawer#fillBuffer}
     */
    public static void start()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
    }

    /**
     * Start drawing tinted textures to a buffer<br>
     * Call before using {@link TextureDrawer#fillBuffer}
     */
    public static void startTinted()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
    }

    /**
     * Start drawing colored rectangles to a buffer<br>
     * Call before using {@link TextureDrawer#fillColorBuffer} or {@link TextureDrawer#fillGuiColorBuffer}
     */
    public static void startColored()
    {
        if (buffer != null) { throw new IllegalStateException("Last drawing operation not finished!"); }

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer = Tesselator.getInstance().getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
    }

    /**
     * Fill the draw buffer with a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillBuffer(PoseStack pstack, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        fillBuffer(pstack, x, y, 0, w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer with a tinted texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillBuffer(PoseStack pstack, float x, float y, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        fillBuffer(pstack, x, y, 0, w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer for a gui with a texture with a size of 256x256
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     */
    public static void fillGuiBuffer(PoseStack pstack, float x, float y, float z, float texX, float texY, float w, float h)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer for a gui with a tinted texture with a size of 256x256
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param texX X offset into the texture
     * @param texY Y offset into the texture
     * @param w Width of the texture segment
     * @param h Height of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillGuiBuffer(PoseStack pstack, float x, float y, float z, float texX, float texY, float w, float h, int color)
    {
        float minU = texX / 256F;
        float maxU = minU + (w / 256F);
        float minV = texY / 256F;
        float maxV = minV + (h / 256F);
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer for a gui with a section of a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param uX X offset into the texture
     * @param vY Y offset into the texture
     * @param uW Width of the texture segment
     * @param vH Height of the texture segment
     */
    public static void fillGuiBufferArb(PoseStack pstack, float x, float y, float z, float uX, float vY, float uW, float vH, float texWidth, float texHeight)
    {
        float minU = uX / texWidth;
        float maxU = minU + (uW / texWidth);
        float minV = vY / texHeight;
        float maxV = minV + (vH / texHeight);
        fillBuffer(pstack, x, y, z, uW, vH, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer for a gui with a tinted section of a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param uX X offset into the texture
     * @param vY Y offset into the texture
     * @param uW Width of the texture segment
     * @param vH Height of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillGuiBufferArb(PoseStack pstack, float x, float y, float z, float uX, float vY, float uW, float vH, float texWidth, float texHeight, int color)
    {
        float minU = uX / texWidth;
        float maxU = minU + (uW / texWidth);
        float minV = vY / texHeight;
        float maxV = minV + (vH / texHeight);
        fillBuffer(pstack, x, y, z, uW, vH, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer for a gui with a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillGuiBuffer(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV);
    }

    /**
     * Fill the draw buffer for a gui with a tinted texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillGuiBuffer(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        fillBuffer(pstack, x, y, z, w, h, minU, maxU, minV, maxV, color);
    }

    /**
     * Fill the draw buffer with a texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     */
    public static void fillBuffer(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        buffer.vertex(pstack.last().pose(), x,     y + h, z).uv(minU, maxV).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y + h, z).uv(maxU, maxV).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y,     z).uv(maxU, minV).endVertex();
        buffer.vertex(pstack.last().pose(), x,     y,     z).uv(minU, minV).endVertex();
    }

    /**
     * Fill the draw buffer with a tinted texture with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position (mostly referred to as blitOffset)
     * @param w Resulting width
     * @param h Resulting height
     * @param minU Min u of the texture segment
     * @param maxU Max u of the texture segment
     * @param minV Min v of the texture segment
     * @param maxV Max v of the texture segment
     * @param color Color to tint the texture in
     */
    public static void fillBuffer(PoseStack pstack, float x, float y, float z, float w, float h, float minU, float maxU, float minV, float maxV, int color)
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        int[] colors = getRGBAArrayFromHexColor(color);
        buffer.vertex(pstack.last().pose(), x,     y + h, z).uv(minU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y + h, z).uv(maxU, maxV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y,     z).uv(maxU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x,     y,     z).uv(minU, minV).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
    }

    /**
     * Fill the draw buffer with a colored rectangle with arbitrary dimensions
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param color Color to tint the texture in
     */

    public static void fillColorBuffer(PoseStack pstack, float x, float y, float z, float w, float h, int color)
    {
        int[] colors = getRGBAArrayFromHexColor(color);
        buffer.vertex(pstack.last().pose(), x,     y + h, z).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y + h, z).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x + w, y,     z).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
        buffer.vertex(pstack.last().pose(), x,     y,     z).color(colors[0], colors[1], colors[2], colors[3]).endVertex();
    }

    /**
     * Fill the draw buffer with a colored rectangle with arbitrary dimensions in a gui
     * @param pstack The PoseStack of the current context
     * @param x X position
     * @param y Y position
     * @param z Z position
     * @param w Resulting width
     * @param h Resulting height
     * @param color Color to tint the texture in
     */

    public static void fillGuiColorBuffer(PoseStack pstack, float x, float y, float z, float w, float h, int color)
    {
        fillColorBuffer(pstack, x, y, z, w, h, color);
    }

    /**
     * Finish drawing textures to a buffer<br>
     * Call after {@link TextureDrawer#start} and one or more {@link TextureDrawer#fillBuffer} calls
     */
    public static void end()
    {
        if (buffer == null) { throw new IllegalStateException("Drawing operation not started!"); }

        BufferUploader.drawWithShader(buffer.end());

        buffer = null;
    }

    private static int[] getRGBAArrayFromHexColor(int color)
    {
        int[] ints = new int[4];
        ints[0] = (color >> 24 & 255);
        ints[1] = (color >> 16 & 255);
        ints[2] = (color >>  8 & 255);
        ints[3] = (color       & 255);
        return ints;
    }
}
