package xfacthd.atlasviewer.platform;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.client.util.SpriteSourceTypeMapper;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;

public final class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public boolean isDevelopmentEnvironment()
    {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDir()
    {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public void fireSpriteSourceDetailsEvent()
    {
        RegisterSpriteSourceDetailsEvent.EVENT.invoker().accept(
                SpriteSourceManager::registerPrimaryResourceGetter,
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        );
    }

    @Override
    public void pushScreenLayer(Screen screen)
    {
        ScreenStacker.pushScreenLayer(screen);
    }

    @Override
    public void popScreenLayer()
    {
        ScreenStacker.popScreenLayer();
    }

    @Override
    @Nullable
    public ScreenRectangle peekScissorState(GuiGraphics graphics)
    {
        return graphics.scissorStack.peek();
    }

    @Override
    public void submitCustomGuiRenderState(GuiGraphics graphics, GuiElementRenderState renderState)
    {
        graphics.guiRenderState.submitGuiElement(renderState);
    }

    @Override
    public String getSpriteSourceName(SpriteSource source)
    {
        return SpriteSourceTypeMapper.getSpriteSourceName(source);
    }

    @Override
    public String getSpriteSourceSimpleName(SpriteSource source)
    {
        String name = getSpriteSourceName(source);
        int lastPeriod = name.lastIndexOf('.');
        if (lastPeriod > -1)
        {
            name = name.substring(lastPeriod + 1);
        }
        return name;
    }

    @Override
    public void registerPlatformSpecificBuiltInSpriteSourceDetails() { }
}
