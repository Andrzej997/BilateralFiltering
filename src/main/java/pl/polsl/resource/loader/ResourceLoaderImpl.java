package pl.polsl.resource.loader;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ResourceLoaderImpl implements ResourceLoader {

    @Override
    public List<File> loadAllFolderResources(String resourcesPath) throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(resourcesPath)) {
            throw new UnsupportedOperationException("resourcesPath should not be null");
        }
        URL resource = this.getClass().getClassLoader().getResource(resourcesPath);
        return Files.walk(Paths.get(resource.toURI()))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .collect(Collectors.toList());
    }
}
