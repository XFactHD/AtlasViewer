package xfacthd.atlasviewer.client.screen.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import org.joml.Matrix3x2f;

import javax.annotation.Nullable;

public record FloatColoredRectangleRenderState(
        RenderPipeline pipeline,
        Matrix3x2f pose,
        float x0,
        float y0,
        float x1,
        float y1,
        int col1,
        int col2,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState
{
    @Override
    public void buildVertices(VertexConsumer consumer, float z)
    {
        consumer.addVertexWith2DPose(pose(), x0(), y0(), z).setColor(col1());
        consumer.addVertexWith2DPose(pose(), x0(), y1(), z).setColor(col2());
        consumer.addVertexWith2DPose(pose(), x1(), y1(), z).setColor(col2());
        consumer.addVertexWith2DPose(pose(), x1(), y0(), z).setColor(col1());
    }

    @Override
    public TextureSetup textureSetup()
    {
        return TextureSetup.noTexture();
    }
}
