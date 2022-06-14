package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

public class ClientUtils
{
    public static void drawNineSliceTexture(Screen screen, PoseStack pstack, int screenX, int screenY, int screenWidth, int screenHeight, int texWidth, int texHeight, int border)
    {
        int texCenterWidth = texWidth - (border * 2);
        int texCenterHeight = texHeight - (border * 2);

        TextureDrawer.start();

        if (border > 0)
        {
            //Corners
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX, screenY, 0, 0, border, border);
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, screenY, texWidth - border, 0, border, border);
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX, screenY + screenHeight - border, 0, texHeight - border, border, border);
            TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, screenY + screenHeight - border, texWidth - border, texHeight - border, border, border);

            //Edges
            for (int i = 0; i <= (screenWidth / texCenterWidth); i++)
            {
                int x = screenX + border + (i * texCenterWidth);
                int width = Math.min(texCenterWidth, screenWidth - (i * texCenterWidth) - (border * 2));
                TextureDrawer.fillGuiBuffer(pstack, screen, x, screenY, border, 0, width, border);
                TextureDrawer.fillGuiBuffer(pstack, screen, x, screenY + screenHeight - border, border, texHeight - border, width, border);
            }
            for (int i = 0; i <= (screenHeight / texCenterHeight); i++)
            {
                int y = screenY + border + (i * texCenterHeight);
                int height = Math.min(texCenterHeight, screenHeight - (i * texCenterHeight) - (border * 2));
                TextureDrawer.fillGuiBuffer(pstack, screen, screenX, y, 0, border, border, height);
                TextureDrawer.fillGuiBuffer(pstack, screen, screenX + screenWidth - border, y, texWidth - border, border, border, height);
            }
        }

        //Center
        int centerWidth = (screenWidth - (border * 2)) / texCenterWidth;
        int centerHeight = (screenHeight - (border * 2)) / texCenterHeight;
        for (int ix = 0; ix <= centerWidth; ix++)
        {
            for (int iy = 0; iy <= centerHeight; iy++)
            {
                int x = screenX + border + (ix * texCenterWidth);
                int y = screenY + border + (iy * texCenterHeight);
                int width = Math.min(texCenterWidth, screenWidth - (ix * texCenterWidth) - (border * 2));
                int height = Math.min(texCenterHeight, screenHeight - (iy * texCenterHeight) - (border * 2));
                TextureDrawer.fillGuiBuffer(pstack, screen, x, y, border, border, width, height);
            }
        }

        TextureDrawer.end();
    }
}
