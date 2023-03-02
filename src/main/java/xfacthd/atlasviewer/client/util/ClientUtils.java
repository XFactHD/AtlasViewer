package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.FormattedText;

public class ClientUtils
{
    public static void drawNineSliceTexture(Screen screen, PoseStack pstack, int elemX, int elemY, int elemW, int elemH, NineSlice ns)
    {
        drawNineSliceTexture(screen, pstack, elemX, elemY, elemW, elemH, ns.uX(), ns.vY(), ns.uW(), ns.vH(), ns.texW(), ns.texH(), ns.border());
    }

    public static void drawNineSliceTexture(
            Screen screen, PoseStack pstack, int elemX, int elemY, int elemW, int elemH, int uX, int vY, int uW, int vH, int texW, int texH, int border
    )
    {
        int texCenterWidth = uW - (border * 2);
        int texCenterHeight = vH - (border * 2);

        TextureDrawer.start();

        if (border > 0)
        {
            //Corners
            TextureDrawer.fillGuiBufferArb(pstack, screen, elemX, elemY, uX, vY, border, border, texW, texH);
            TextureDrawer.fillGuiBufferArb(pstack, screen, elemX + elemW - border, elemY, uX + uW - border, vY, border, border, texW, texH);
            TextureDrawer.fillGuiBufferArb(pstack, screen, elemX, elemY + elemH - border, uX, vY + vH - border, border, border, texW, texH);
            TextureDrawer.fillGuiBufferArb(pstack, screen, elemX + elemW - border, elemY + elemH - border, uX + uW - border, vY + vH - border, border, border, texW, texH);

            //Edges
            for (int i = 0; i <= (elemW / texCenterWidth); i++)
            {
                int x = elemX + border + (i * texCenterWidth);
                int width = Math.min(texCenterWidth, elemW - (i * texCenterWidth) - (border * 2));
                TextureDrawer.fillGuiBufferArb(pstack, screen, x, elemY, uX + border, vY, width, border, texW, texH);
                TextureDrawer.fillGuiBufferArb(pstack, screen, x, elemY + elemH - border, uX + border, vY + vH - border, width, border, texW, texH);
            }
            for (int i = 0; i <= (elemH / texCenterHeight); i++)
            {
                int y = elemY + border + (i * texCenterHeight);
                int height = Math.min(texCenterHeight, elemH - (i * texCenterHeight) - (border * 2));
                TextureDrawer.fillGuiBufferArb(pstack, screen, elemX, y, uX, vY + border, border, height, texW, texH);
                TextureDrawer.fillGuiBufferArb(pstack, screen, elemX + elemW - border, y, uX + uW - border, vY + border, border, height, texW, texH);
            }
        }

        //Center
        int centerWidth = (elemW - (border * 2)) / texCenterWidth;
        int centerHeight = (elemH - (border * 2)) / texCenterHeight;
        for (int ix = 0; ix <= centerWidth; ix++)
        {
            for (int iy = 0; iy <= centerHeight; iy++)
            {
                int x = elemX + border + (ix * texCenterWidth);
                int y = elemY + border + (iy * texCenterHeight);
                int width = Math.min(texCenterWidth, elemW - (ix * texCenterWidth) - (border * 2));
                int height = Math.min(texCenterHeight, elemH - (iy * texCenterHeight) - (border * 2));
                TextureDrawer.fillGuiBufferArb(pstack, screen, x, y, uX + border, vY + border, width, height, texW, texH);
            }
        }

        TextureDrawer.end();
    }

    public static int getWrappedHeight(Font font, FormattedText text, int width)
    {
        return font.split(text, width).size() * font.lineHeight;
    }

    public static void drawColoredBox(Screen screen, PoseStack poseStack, float x, float y, float w, float h, int color)
    {
        TextureDrawer.fillGuiColorBuffer(poseStack, screen, x,          y         , 1F,  h, color);
        TextureDrawer.fillGuiColorBuffer(poseStack, screen, x + w - 1F, y         , 1F,  h, color);
        TextureDrawer.fillGuiColorBuffer(poseStack, screen, x,          y         ,  w, 1F, color);
        TextureDrawer.fillGuiColorBuffer(poseStack, screen, x,          y + h - 1F,  w, 1F, color);
    }
}
