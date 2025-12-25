package xfacthd.atlasviewer.client.screen.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public record FloatBlitRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        float x0,
        float y0,
        float x1,
        float y1,
        float u0,
        float u1,
        float v0,
        float v1,
        int color,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    @Override
    public void buildVertices(VertexConsumer consumer)
    {
        consumer.addVertexWith2DPose(pose(), x0(), y0()).setUv(u0(), v0()).setColor(color());
        consumer.addVertexWith2DPose(pose(), x0(), y1()).setUv(u0(), v1()).setColor(color());
        consumer.addVertexWith2DPose(pose(), x1(), y1()).setUv(u1(), v1()).setColor(color());
        consumer.addVertexWith2DPose(pose(), x1(), y0()).setUv(u1(), v0()).setColor(color());
    }
}
