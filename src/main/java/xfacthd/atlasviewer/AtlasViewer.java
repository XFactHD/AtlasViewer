package xfacthd.atlasviewer;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(AtlasViewer.MOD_ID)
public class AtlasViewer
{
    public static final String MOD_ID = "atlasviewer";
    public static final Logger LOGGER = LogUtils.getLogger();

    public AtlasViewer()
    {
        ModLoadingContext.get().registerExtensionPoint(
                IExtensionPoint.DisplayTest.class,
                () -> new IExtensionPoint.DisplayTest(
                        () -> "dontcare",
                        (remote, network) -> network
                )
        );
    }
}
