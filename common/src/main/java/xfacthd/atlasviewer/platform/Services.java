package xfacthd.atlasviewer.platform;

import xfacthd.atlasviewer.AtlasViewer;
import xfacthd.atlasviewer.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

public final class Services
{
    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz)
    {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        AtlasViewer.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }



    private Services() { }
}