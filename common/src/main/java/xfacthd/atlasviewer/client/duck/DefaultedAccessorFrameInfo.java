package xfacthd.atlasviewer.client.duck;

import xfacthd.atlasviewer.client.mixin.AccessorFrameInfo;

public interface DefaultedAccessorFrameInfo extends AccessorFrameInfo
{
    @Override
    default int atlasviewer$getTime() { throw new UnsupportedOperationException("Not injected"); }
}
