package xfacthd.atlasviewer.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;
import java.util.List;

public final class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getGameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public void fireSpriteSourceDetailsEvent() {
        RegisterSpriteSourceDetailsEvent.EVENT.invoker().accept(
                SpriteSourceManager::registerPrimaryResourceGetter,
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        );
    }

    @Override
    public void pushScreenLayer(Screen screen) {
        ScreenStacker.pushScreenLayer(screen);
    }

    @Override
    public void popScreenLayer() {
        ScreenStacker.popScreenLayer();
    }

    @Override
    public @Nullable ScreenRectangle peekScissorState(GuiGraphicsExtractor graphics) {
        return graphics.scissorStack.peek();
    }

    @Override
    public void submitCustomGuiRenderState(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
        graphics.guiRenderState.addGuiElement(renderState);
    }

    @Override
    public void registerPlatformSpecificBuiltInSpriteSourceDetails() { }

    @Override
    public void setTooltip(Font font, GuiGraphicsExtractor graphics, Component component, int mouseX, int mouseY) {
        Tooltip tooltip = Tooltip.create(component);
        List<FormattedCharSequence> lines = tooltip.toCharSequence(Minecraft.getInstance());
        graphics.setTooltipForNextFrame(lines, mouseX, mouseY);
    }
}
