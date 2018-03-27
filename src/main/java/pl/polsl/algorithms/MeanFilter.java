package pl.polsl.algorithms;

import pl.polsl.domain.CustomColor;
import pl.polsl.domain.Image;

import java.util.Hashtable;

class MeanFilter implements ImageFilter {

    private final int kernelSize; //rozmiar maski filtra

    MeanFilter(int kernelSize) {
        this.kernelSize = kernelSize;
    }

    MeanFilter(Hashtable<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Object kernelSizeTemp = params.get("kernelSize");
            if (kernelSizeTemp != null) {
                kernelSize = (Integer) kernelSizeTemp;
            } else {
                kernelSize = -1;
            }
        } else {
            kernelSize = -1;
        }
    }

    @Override
    public Image applyFilter(Image input) {
        Image output = input.clone();
        input.stream().parallel().forEach(customColor -> {
            CustomColor[][] pointNeighbourhood = input.getPointNeighbourhood(customColor.getX(), customColor.getY(), kernelSize);
            CustomColor outputColor = applyFilterInternal(customColor, pointNeighbourhood);
            output.replaceColorValue(customColor.getX(), customColor.getY(), outputColor);
        });
        return output;
    }

    private CustomColor applyFilterInternal(CustomColor input, CustomColor[][] neighbourhoodMatrix) {
        double sumR = 0.0;
        double sumG = 0.0;
        double sumB = 0.0;

        for (int i = 0; i < neighbourhoodMatrix.length; i++) {
            for (int j = 0; j < neighbourhoodMatrix[i].length; j++) {
                CustomColor neighbour = neighbourhoodMatrix[i][j];
                sumR += neighbour.getRed();
                sumG += neighbour.getGreen();
                sumB += neighbour.getBlue();
            }
        }

        int resultRed = ((byte) (sumR / (double) (kernelSize * kernelSize))) & 0xFF;
        int resultGreen = ((byte) (sumG / (double) (kernelSize * kernelSize))) & 0xFF;
        int resultBlue = ((byte) (sumB / (double) (kernelSize * kernelSize))) & 0xFF;

        return new CustomColor(resultRed, resultGreen, resultBlue, input.getX(), input.getY());
    }
}
