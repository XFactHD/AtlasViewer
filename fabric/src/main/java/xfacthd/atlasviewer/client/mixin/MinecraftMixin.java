package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.main.GameConfig;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mixin(Minecraft.class)
@SuppressWarnings({ "MethodMayBeStatic", "DataFlowIssue" })
public final class MinecraftMixin
{
    @Shadow @Final private Window window;

    @Inject(
            method = "resizeDisplay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;resize(Lnet/minecraft/client/Minecraft;II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void atlasviewer$onScreenResized(CallbackInfo ci)
    {
        ScreenStacker.onScreenResize((Minecraft)(Object) this, window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }

    @Inject(
            method = "setScreen",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;screen:Lnet/minecraft/client/gui/screens/Screen;",
                    opcode = Opcodes.GETFIELD,
                    shift = At.Shift.BEFORE,
                    ordinal = 0
            )
    )
    private void atlasviewer$onSetScreen(Screen guiScreen, CallbackInfo ci)
    {
        ScreenStacker.clearScreenStack((Minecraft)(Object) this);
    }

    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/Window;updateVsync(Z)V")
    )
    private void atlasviewer$handlePreResourceLoadInit(GameConfig gameConfig, CallbackInfo ci)
    {
        SpriteSourceManager.registerDetails();
    }
}
