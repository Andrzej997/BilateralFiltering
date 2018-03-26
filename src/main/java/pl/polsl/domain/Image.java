package pl.polsl.domain;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

public interface Image extends Cloneable, Iterable<CustomColor> {

    void replaceColorValue(int x, int y, CustomColor color);

    File toFile(String writePath) throws IOException;

    int getWidth();

    int getHeight();

    int getType();

    CustomColor[][] getColorsMap();

    String getFormat();

    Stream<CustomColor> stream();

    Image clone();

    CustomColor[][] getPointNeighbourhood(int x, int y, int kernelSize);
}
