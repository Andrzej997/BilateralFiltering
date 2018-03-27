package pl.polsl.domain;

public enum ImageTypesEnum {
    BASIC;

    static public ImageTypesEnum get(String name) {
        for (ImageTypesEnum imageTypesEnum : ImageTypesEnum.values()) {
            if (imageTypesEnum.name().equalsIgnoreCase(name)) {
                return imageTypesEnum;
            }
        }
        return null;
    }
}
