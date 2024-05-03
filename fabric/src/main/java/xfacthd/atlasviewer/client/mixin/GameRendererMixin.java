package xfacthd.atlasviewer.client.mixin;

import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import xfacthd.atlasviewer.client.screen.stacking.ScreenStacker;

@Mixin(GameRenderer.class)
@SuppressWarnings("MethodMayBeStatic")
public final class GameRendererMixin
{
    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4f;setOrtho(FFFFFF)Lorg/joml/Matrix4f;",
                    remap = false
            ),
            index = 5
    )
    private float atlasviewer$modifyGuiFarPlaneInMatrix(float farPlane)
    {
        return ScreenStacker.isNonEmpty() ? ScreenStacker.getGuiFarPlane() : farPlane;
    }

    @ModifyArg(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4fStack;translation(FFF)Lorg/joml/Matrix4f;",
                    remap = false
            ),
            index = 2
    )
    private float atlasviewer$modifyGuiFarPlaneInTranslation(float farPlane)
    {
        return ScreenStacker.isNonEmpty() ? (10000F - ScreenStacker.getGuiFarPlane()) : farPlane;
    }
}
