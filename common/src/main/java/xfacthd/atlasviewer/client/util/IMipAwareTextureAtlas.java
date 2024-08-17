package xfacthd.atlasviewer.client.util;

public interface IMipAwareTextureAtlas
{
    default void atlasviewer$setMipMapEnabled(boolean enabled) { throw new UnsupportedOperationException("Not injected"); }

    default boolean atlasviewer$isMipMapEnabled() { throw new UnsupportedOperationException("Not injected"); }
}
