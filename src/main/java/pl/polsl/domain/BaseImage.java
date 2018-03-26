package pl.polsl.domain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

abstract class BaseImage extends BufferedImage implements Image {

    BaseImage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    BaseImage(BufferedImage bufferedImage, Hashtable<String, Object> properties) {
        super(bufferedImage.getColorModel(), bufferedImage.getRaster(), bufferedImage.isAlphaPremultiplied(), properties);
    }

    public abstract void replaceColorValue(int x, int y, CustomColor color);

    public abstract File toFile(String writePath) throws IOException;

    public int getWidth() {
        return super.getWidth();
    }

    public int getHeight() {
        return super.getHeight();
    }

    public int getType() {
        return super.getType();
    }

    public abstract CustomColor[][] getColorsMap();

    public abstract String getFormat();

    @Override
    public Iterator<CustomColor> iterator() {
        return new ColorIterator();
    }

    private class ColorIterator implements Iterator<CustomColor> {
        int cursor;

        @Override
        public boolean hasNext() {
            return cursor != getColorsMap().length;
        }

        @Override
        public CustomColor next() {
            try {
                int idx = cursor;
                int i = idx / getWidth();
                int j = idx % getHeight();
                CustomColor color = getColorsMap()[i][j];
                cursor = idx + 1;
                return color;
            } catch (IndexOutOfBoundsException e) {
                throw new NoSuchElementException();
            }
        }
    }

    @Override
    public Image clone() {
        Image clone = new BasicImageImpl(this.getWidth(), this.getHeight(), this.getType(), this.getFormat());
        for (int i = 0; i < this.getWidth(); i++) {
            for (int j = 0; j < this.getHeight(); j++) {
                clone.replaceColorValue(i, j, new CustomColor(this.getColorsMap()[i][j].getRGB(), i, j));
            }
        }
        return clone;
    }

    @Override
    public abstract CustomColor[][] getPointNeighbourhood(int x, int y, int kernelSize);

    public Stream<CustomColor> stream() {
        return Arrays.stream(getColorsMap()).flatMap(Arrays::stream);
    }

    @Override
    public String toString() {
        return "BaseImage{} " + super.toString();
    }
}
