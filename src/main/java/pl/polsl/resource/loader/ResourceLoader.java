package pl.polsl.resource.loader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface ResourceLoader {
    List<File> loadAllFolderResources(String resourcesPath) throws IOException, URISyntaxException;
}
