package xfacthd.atlasviewer.client.duck.spritesources;

import xfacthd.atlasviewer.client.mixin.spritesources.AccessorDirectoryLister;

public interface DefaultedAccessorDirectoryLister extends AccessorDirectoryLister
{
    default String atlasviewer$getSourcePath() { throw new UnsupportedOperationException("Not injected"); }

    default String atlasviewer$getIdPrefix() { throw new UnsupportedOperationException("Not injected"); }
}
