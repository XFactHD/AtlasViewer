package xfacthd.atlasviewer.platform.services;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import xfacthd.atlasviewer.client.util.WrappedSpriteSource;

import java.nio.file.Path;

public interface IPlatformHelper
{
    boolean isDevelopmentEnvironment();

    Path getGameDir();

    void fireSpriteSourceDetailsEvent();

    void pushScreenLayer(Screen screen);

    void popScreenLayer();

    @Nullable
    ScreenRectangle peekScissorState(GuiGraphics graphics);

    void submitCustomGuiRenderState(GuiGraphics graphics, GuiElementRenderState renderState);

    String getSpriteSourceName(SpriteSource source);

    String getSpriteSourceSimpleName(SpriteSource source);

    void registerPlatformSpecificBuiltInSpriteSourceDetails();

    /**
     * Resets the texture's mip level parameters to the default values to ensure texture dumping works properly
     */
    default void fixMipLevelTexParams(GpuTexture srcTexture)
    {
        if (srcTexture instanceof GlTexture glTex)
        {
            GlStateManager._bindTexture(glTex.glId());
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_BASE_LEVEL, 0);
            GlStateManager._texParameter(GL11.GL_TEXTURE_2D, GL12.GL_TEXTURE_MAX_LEVEL, srcTexture.getMipLevels() - 1);
            GlStateManager._bindTexture(0);
        }
    }

    default SpriteSource wrapSpriteSource(SpriteSource original)
    {
        return new WrappedSpriteSource(original);
    }

    void setTooltip(Font font, GuiGraphics graphics, Component component, int mouseX, int mouseY);
}
