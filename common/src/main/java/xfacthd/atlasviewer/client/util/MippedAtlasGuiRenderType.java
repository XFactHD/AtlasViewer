package xfacthd.atlasviewer.client.util;

import com.mojang.blaze3d.opengl.GlTexture;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderPipelines;
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
                    1536,
                    RenderPipelines.GUI_TEXTURED,
                    RenderType.CompositeState.builder()
                            .setTextureState(new MippedAtlasTextureStateShard(atlas, mip))
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
                setMipBaseLevel(atlas, mipLevel);
                RenderSystem.setShaderTexture(0, atlas.getTexture());
            }, () -> setMipBaseLevel(atlas, 0));
        }

        private static void setMipBaseLevel(TextureAtlas atlas, int mipLevel)
        {
            GlStateManager._activeTexture(GL13.GL_TEXTURE0);
            GlStateManager._bindTexture(((GlTexture) atlas.getTexture()).glId());
            GlStateManager._texParameter(GL13.GL_TEXTURE_2D, GL13.GL_TEXTURE_BASE_LEVEL, mipLevel);
        }
    }

    private MippedAtlasGuiRenderType() { }
}
