package xfacthd.atlasviewer.platform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import org.jspecify.annotations.Nullable;
import xfacthd.atlasviewer.client.AVClientNeoForge;
import xfacthd.atlasviewer.client.api.RegisterSpriteSourceDetailsEvent;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;
import xfacthd.atlasviewer.client.util.WrappedSpriteSourceNeoForge;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class NeoForgePlatformHelper implements IPlatformHelper {
    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.getCurrent().isProduction();
    }

    @Override
    public Path getGameDir() {
        return FMLPaths.GAMEDIR.get();
    }

    @Override
    public void fireSpriteSourceDetailsEvent() {
        ModLoader.postEvent(new RegisterSpriteSourceDetailsEvent(
                SpriteSourceManager::registerPrimaryResourceGetter,
                SpriteSourceManager::registerSourceStringifier,
                SpriteSourceManager::registerSimpleSourceStringifier,
                SpriteSourceManager::registerSpecialSourceDescription,
                SpriteSourceManager::registerSourceTooltipAppender
        ));
    }

    @Override
    public void pushScreenLayer(Screen screen) {
        Minecraft.getInstance().gui.pushScreenLayer(screen);
    }

    @Override
    public void popScreenLayer() {
        Minecraft.getInstance().gui.popScreenLayer();
    }

    @Override
    public @Nullable ScreenRectangle peekScissorState(GuiGraphicsExtractor graphics) {
        return graphics.peekScissorStack();
    }

    @Override
    public void submitCustomGuiRenderState(GuiGraphicsExtractor graphics, GuiElementRenderState renderState) {
        graphics.submitGuiElementRenderState(renderState);
    }

    @Override
    public void registerPlatformSpecificBuiltInSpriteSourceDetails() {
        AVClientNeoForge.registerBuiltInSpriteSourceDetails();
    }

    @Override
    public SpriteSource wrapSpriteSource(SpriteSource original) {
        return new WrappedSpriteSourceNeoForge(original);
    }

    @Override
    public void setTooltip(Font font, GuiGraphicsExtractor graphics, Component component, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(font, List.of(component), Optional.empty(), mouseX, mouseY);
    }
}
