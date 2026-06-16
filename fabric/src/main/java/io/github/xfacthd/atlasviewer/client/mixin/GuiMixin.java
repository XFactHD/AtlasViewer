package io.github.xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.github.xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;

@Mixin(Gui.class)
@SuppressWarnings("DataFlowIssue")
public class GuiMixin {
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
    private void atlasviewer$onSetScreen(Screen guiScreen, CallbackInfo ci) {
        ScreenStacker.clearScreenStack((Gui) (Object) this);
    }
}
