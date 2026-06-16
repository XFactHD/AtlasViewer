package io.github.xfacthd.atlasviewer.client.util;

public interface IVisibilitySetter {
    default void atlasviewer$setVisible(boolean visible) {
        throw new UnsupportedOperationException("Not injected");
    }
}
