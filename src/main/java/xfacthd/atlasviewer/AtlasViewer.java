package xfacthd.atlasviewer;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(AtlasViewer.MOD_ID)
public class AtlasViewer
{
    public static final String MOD_ID = "atlasviewer";

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
