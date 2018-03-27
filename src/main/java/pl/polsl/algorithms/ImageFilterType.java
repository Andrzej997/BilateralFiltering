package pl.polsl.algorithms;

public enum ImageFilterType {

    MEAN,
    GAUSS_BILATERAL;

    public static ImageFilterType get(String name) {
        for (ImageFilterType imageFilterType : ImageFilterType.values()) {
            if (imageFilterType.name().equalsIgnoreCase(name)) {
                return imageFilterType;
            }
        }
        return null;
    }
}
