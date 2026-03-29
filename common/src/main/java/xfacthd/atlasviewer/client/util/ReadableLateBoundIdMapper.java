package xfacthd.atlasviewer.client.util;

import java.util.Map;
import java.util.stream.Stream;

public interface ReadableLateBoundIdMapper<I, V> {
    default V atlasviewer$get(I key) {
        throw new UnsupportedOperationException("Not injected");
    }

    default I atlasviewer$getKey(V value) {
        throw new UnsupportedOperationException("Not injected");
    }

    default Stream<Map.Entry<I, V>> atlasviewer$stream() {
        throw new UnsupportedOperationException("Not injected");
    }
}
