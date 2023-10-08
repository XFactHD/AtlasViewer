package xfacthd.atlasviewer.client.util;

import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.server.packs.resources.ResourceManager;

public final class MissingTextureDummySpriteSource implements SpriteSource
{
    public static final MissingTextureDummySpriteSource INSTANCE = new MissingTextureDummySpriteSource();

    private MissingTextureDummySpriteSource() { }

    @Override
    public void run(ResourceManager pResourceManager, Output pOutput)
    {
        throw new UnsupportedOperationException("Dummy");
    }

    @Override
    public SpriteSourceType type()
    {
        throw new UnsupportedOperationException("Dummy");
    }
}
