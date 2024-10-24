package xfacthd.atlasviewer.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xfacthd.atlasviewer.client.util.IGuiGraphicsExtension;

import java.util.function.Function;

@Mixin(GuiGraphics.class)
public class MixinGuiGraphics implements IGuiGraphicsExtension
{
    @Shadow
    @Final
    private PoseStack pose;
    @Shadow
    @Final
    private MultiBufferSource.BufferSource bufferSource;

    @Override
    public void atlasviewer$fill(RenderType renderType, float minX, float minY, float maxX, float maxY, float z, int color)
    {
        if (maxX < minX)
        {
            float temp = minX;
            minX = maxX;
            maxX = temp;
        }

        if (maxY < minY)
        {
            float temp = minY;
            minY = maxY;
            maxY = temp;
        }

        Matrix4f pose = this.pose.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(renderType);
        buffer.addVertex(pose, minX, minY, z).setColor(color);
        buffer.addVertex(pose, minX, maxY, z).setColor(color);
        buffer.addVertex(pose, maxX, maxY, z).setColor(color);
        buffer.addVertex(pose, maxX, minY, z).setColor(color);
    }

    @Override
    public void atlasviewer$innerBlit(
            Function<ResourceLocation, RenderType> renderType,
            ResourceLocation texture,
            float minX,
            float maxX,
            float minY,
            float maxY,
            float minU,
            float maxU,
            float minV,
            float maxV,
            int color
    ) {
        Matrix4f pose = this.pose.last().pose();
        VertexConsumer buffer = bufferSource.getBuffer(renderType.apply(texture));
        buffer.addVertex(pose, minX, minY, 0F).setUv(minU, minV).setColor(color);
        buffer.addVertex(pose, minX, maxY, 0F).setUv(minU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, maxY, 0F).setUv(maxU, maxV).setColor(color);
        buffer.addVertex(pose, maxX, minY, 0F).setUv(maxU, minV).setColor(color);
    }
}
