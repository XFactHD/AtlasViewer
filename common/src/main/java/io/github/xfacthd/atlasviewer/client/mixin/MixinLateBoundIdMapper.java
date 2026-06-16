package io.github.xfacthd.atlasviewer.client.mixin;

import com.google.common.collect.BiMap;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import io.github.xfacthd.atlasviewer.client.util.ReadableLateBoundIdMapper;

import java.util.Map;
import java.util.stream.Stream;

@Mixin(ExtraCodecs.LateBoundIdMapper.class)
public class MixinLateBoundIdMapper<I, V> implements ReadableLateBoundIdMapper<I, V> {
    @Shadow
    @Final
    private BiMap<I, V> idToValue;

    @Override
    public V atlasviewer$get(I key) {
        return idToValue.get(key);
    }

    @Override
    public I atlasviewer$getKey(V value) {
        return idToValue.inverse().get(value);
    }

    @Override
    public Stream<Map.Entry<I, V>> atlasviewer$stream() {
        return idToValue.entrySet().stream();
    }
}
