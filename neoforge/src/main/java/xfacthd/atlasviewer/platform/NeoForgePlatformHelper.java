package xfacthd.atlasviewer.platform;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.blaze3d.validation.ValidationGpuTexture;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.client.AVClientNeoForge;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.client.util.WrappedSpriteSourceNeoForge;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;

public final class NeoForgePlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isDevelopmentEnvironment()
    {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getGameDir()
    {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public void fireSpriteSourceDetailsEvent()
    {
        ModLoader.postEvent(new RegisterSpriteSourceDetailsEvent(
                SpriteSourceManager::registerPrimaryResourceGetter,
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        ));
    }

    @Override
    public void pushScreenLayer(Screen screen)
    {
        Minecraft.getInstance().pushGuiLayer(screen);
    }

    @Override
    public void popScreenLayer()
    {
        Minecraft.getInstance().popGuiLayer();
    }

    @Override
    @Nullable
    public ScreenRectangle peekScissorState(GuiGraphics graphics)
    {
        return graphics.peekScissorStack();
    }

    @Override
    public void submitCustomGuiRenderState(GuiGraphics graphics, GuiElementRenderState renderState)
    {
        graphics.submitGuiElementRenderState(renderState);
    }

    @Override
    public String getSpriteSourceName(SpriteSource source)
    {
        return source.getClass().getName();
    }

    @Override
    public String getSpriteSourceSimpleName(SpriteSource source)
    {
        return source.getClass().getSimpleName();
    }

    @Override
    public void registerPlatformSpecificBuiltInSpriteSourceDetails()
    {
        AVClientNeoForge.registerBuiltInSpriteSourceDetails();
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void fixMipLevelTexParams(GpuTexture srcTexture)
    {
        if (srcTexture instanceof ValidationGpuTexture validationTex)
        {
            srcTexture = validationTex.getRealTexture();
        }
        IPlatformHelper.super.fixMipLevelTexParams(srcTexture);
    }

    @Override
    public SpriteSource wrapSpriteSource(SpriteSource original)
    {
        return new WrappedSpriteSourceNeoForge(original);
    }
}
