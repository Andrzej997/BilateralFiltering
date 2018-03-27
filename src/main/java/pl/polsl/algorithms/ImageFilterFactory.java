package pl.polsl.algorithms;

import java.util.Hashtable;

public class ImageFilterFactory {

    public ImageFilter provideFilter(ImageFilterType filterType, Hashtable<String, Object> properties) {
        switch (filterType) {
            case MEAN:
                return new MeanFilter(properties);
            case GAUSS_BILATERAL:
                return new GaussImageFilter(properties);
        }
        return null;
    }
}
