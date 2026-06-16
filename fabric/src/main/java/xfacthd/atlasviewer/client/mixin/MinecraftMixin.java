package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;
import xfacthd.atlasviewer.client.util.SpriteSourceManager;

@Mixin(Minecraft.class)
@SuppressWarnings({ "MethodMayBeStatic" })
public final class MinecraftMixin {
    @Shadow
    @Final
    private Window window;

    @Inject(
            method = "resizeGui",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screens/Screen;resize(II)V",
                    shift = At.Shift.AFTER
            )
    )
    private void atlasviewer$onScreenResized(CallbackInfo ci) {
        ScreenStacker.onScreenResize(window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }

    @Inject(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;resizeGui()V")
    )
    private void atlasviewer$handlePreResourceLoadInit(GameConfig gameConfig, CallbackInfo ci) {
        SpriteSourceManager.registerDetails();
    }
}
