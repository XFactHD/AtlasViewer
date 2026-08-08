package io.github.xfacthd.atlasviewer.client.screen.state;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.renderer.state.gui.GuiElementRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

import java.util.List;

public record MultiBlitRenderState(
        RenderPipeline pipeline,
        TextureSetup textureSetup,
        Matrix3x2f pose,
        List<Quad> quads,
        int color,
        @Nullable ScreenRectangle scissorArea,
        @Nullable ScreenRectangle bounds
) implements GuiElementRenderState {
    @Override
    public void buildVertices(VertexConsumer consumer) {
        for (Quad quad : quads) {
            consumer.addVertexWith2DPose(pose, quad.x0, quad.y0).setUv(quad.u0, quad.v0).setColor(color);
            consumer.addVertexWith2DPose(pose, quad.x0, quad.y1).setUv(quad.u0, quad.v1).setColor(color);
            consumer.addVertexWith2DPose(pose, quad.x1, quad.y1).setUv(quad.u1, quad.v1).setColor(color);
            consumer.addVertexWith2DPose(pose, quad.x1, quad.y0).setUv(quad.u1, quad.v0).setColor(color);
        }
    }

    public record Quad(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        public Quad(float x, float y, float width, float height) {
            this(x, y, x + width, y + height, 0F, 0F, 1F, 1F);
        }

        public Quad(float x, float y, float width, float height, TextureAtlasSprite sprite) {
            this(x, y, x + width, y + height, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
        }
    }
}
