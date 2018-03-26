package pl.polsl.domain;

import java.io.File;
import java.io.IOException;

public class ImageFactory {

    public Image getImageFromFile(ImageTypesEnum type, File file) throws IOException {
        if (type == ImageTypesEnum.BASIC) {
            return new BasicImageImpl.ImageBuilder().withFile(file).build();
        }
        return null;
    }
}
