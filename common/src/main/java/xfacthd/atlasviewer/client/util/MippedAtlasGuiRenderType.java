package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import org.lwjgl.opengl.GL13;

import java.util.function.BiFunction;

public final class MippedAtlasGuiRenderType
{
    private static final BiFunction<TextureAtlas, Integer, RenderType> FACTORY = Util.memoize((atlas, mip) ->
            RenderType.create(
                    "mipped_atlas_gui",
                    DefaultVertexFormat.POSITION_TEX_COLOR,
                    VertexFormat.Mode.QUADS,
                    1536,
                    RenderType.CompositeState.builder()
                            .setTextureState(new MippedAtlasTextureStateShard(atlas, mip))
                            .setShaderState(RenderStateShard.POSITION_TEXTURE_COLOR_SHADER)
                            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                            .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                            .createCompositeState(false)
            )
    );

    public static RenderType get(TextureAtlas atlas, int mipLevel)
    {
        return FACTORY.apply(atlas, mipLevel);
    }

    private static final class MippedAtlasTextureStateShard extends RenderStateShard.EmptyTextureStateShard
    {
        public MippedAtlasTextureStateShard(TextureAtlas atlas, int mipLevel)
        {
            super(() ->
            {
                RenderSystem.activeTexture(GL13.GL_TEXTURE0);
                RenderSystem.bindTexture(atlas.getId());
                RenderSystem.texParameter(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, mipLevel);
                RenderSystem.setShaderTexture(0, atlas.getId());
            }, () ->
            {
                RenderSystem.activeTexture(GL13.GL_TEXTURE0);
                RenderSystem.bindTexture(atlas.getId());
                RenderSystem.texParameter(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, 0);
            });
        }
    }

    private MippedAtlasGuiRenderType() { }
}
