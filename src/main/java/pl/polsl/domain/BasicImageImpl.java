package pl.polsl.domain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Objects;

class BasicImageImpl extends BaseImage implements Image {

    private CustomColor[][] colors;
    private String format;

    BasicImageImpl(int width, int height, int imageType, String format) {
        super(width, height, imageType);
        this.colors = new CustomColor[width][height];
        this.format = format;
    }

    BasicImageImpl(BufferedImage bufferedImage, Hashtable<String, Object> properties) {
        super(bufferedImage, properties);
    }

    private void loadColors() {
        colors = new CustomColor[this.getWidth()][this.getHeight()];
        for (int x = 0; x < this.getWidth(); x++) {
            for (int y = 0; y < this.getHeight(); y++) {
                colors[x][y] = new CustomColor(this.getRGB(x, y), x, y);
            }
        }
    }

    @Override
    public void replaceColorValue(int x, int y, CustomColor color) {
        colors[x][y] = color;
        this.setRGB(x, y, color.getRGB());
    }

    @Override
    public File toFile(String writePath) throws IOException {
        File f = new File(writePath);
        ImageIO.write(this, format, f);
        return f;
    }

    @Override
    public CustomColor[][] getColorsMap() {
        return colors;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public CustomColor[][] getPointNeighbourhood(int x, int y, int kernelSize) {
        if (kernelSize % 2 == 0 || x < 0 || y < 0 || x > colors.length - 1 || y > colors[0].length - 1) {
            throw new ChybaCiePojebaloException();
        }
        int halfKernel = kernelSize / 2;
        int i = x - halfKernel;
        int imax = kernelSize;
        if (i < 0) {
            imax = halfKernel + x;
            i = 0;
        }
        if (i + kernelSize >= colors.length - 1) {
            imax = halfKernel + colors.length - 1 - x;
        }
        int j = y - halfKernel;
        int ymax = kernelSize;
        if (j < 0) {
            ymax = halfKernel + y;
            j = 0;
        }
        if (j + kernelSize >= colors[0].length - 1) {
            ymax = halfKernel + colors[0].length - 1 - y;
        }
        CustomColor[][] result = new CustomColor[imax][ymax];
        for (int ires = 0; ires < imax; ires++) {
            for (int jres = 0; jres < ymax; jres++) {
                result[ires][jres] = colors[i + ires][j + jres];
            }
        }
        return result;
    }

    static class ImageBuilder {
        private BasicImageImpl basicImageImpl;

        ImageBuilder withFile(File file) throws IOException {
            BufferedImage image = ImageIO.read(file);
            basicImageImpl = new BasicImageImpl(image, prepareProperties(image));
            String name = file.getName();
            basicImageImpl.format = name.substring(name.lastIndexOf(".") + 1);
            if (basicImageImpl != null) {
                basicImageImpl.loadColors();
            }
            return this;
        }

        private Hashtable<String, Object> prepareProperties(BufferedImage image) {
            String[] propertyNames = image.getPropertyNames();
            Hashtable<String, Object> hashtable = null;
            if (propertyNames != null && propertyNames.length > 0) {
                hashtable = new Hashtable<>();
                for (String propertyName : propertyNames) {
                    hashtable.put(propertyName, image.getProperty(propertyName));
                }
            }
            return hashtable;
        }

        Image build() {
            return basicImageImpl;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BasicImageImpl colors1 = (BasicImageImpl) o;
        return Arrays.equals(colors, colors1.colors) &&
                Objects.equals(format, colors1.format);
    }

    @Override
    public int hashCode() {

        int result = Objects.hash(format);
        result = 31 * result + Arrays.hashCode(colors);
        return result;
    }

    @Override
    public String toString() {
        return "BasicImageImpl{" +
                "colors=" + Arrays.toString(colors) +
                ", format='" + format + '\'' +
                "} " + super.toString();
    }
}
