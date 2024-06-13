package xfacthd.atlasviewer;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class AtlasViewer
{
    public static final String MOD_ID = "atlasviewer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static ResourceLocation rl(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }



    private AtlasViewer() { }
}
