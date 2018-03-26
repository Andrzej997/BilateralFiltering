package pl.polsl.domain;

public class CustomColor extends java.awt.Color {

    private final int x;
    private final int y;

    public CustomColor(int r, int g, int b, int x, int y) {
        super(r, g, b);
        this.x = x;
        this.y = y;
    }

    CustomColor(int rgb, int x, int y) {
        super(rgb);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Integer getColor(Color color) {
        if (color == null) {
            return null;
        }
        switch (color) {
            case B:
                return getBlue();
            case G:
                return getGreen();
            case R:
                return getRed();
        }
        return null;
    }

    public enum Color {
        R, G, B
    }
}
