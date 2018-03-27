package pl.polsl.algorithms;

import pl.polsl.domain.CustomColor;
import pl.polsl.domain.Image;

import java.util.Hashtable;

import static java.lang.Math.*;

class GaussImageFilter implements ImageFilter {

    private static final int COLOR_DEPTH = 256;

    private final int kernelSize; //rozmiar maski filtra
    private final double sigmaD; // współczynnik bliskości pikseli
    private final double sigmaR; //współczynnik skali podobieństwa

    private double[][] closenessFuncValues; //wartości funkcji bliskości pikseli
    private double[][] similarityFuncValues; //wartości funkcji podobieństwa piseli

    GaussImageFilter(int kernelSize) {
        this.kernelSize = kernelSize;
        sigmaD = 10;
        sigmaR = 300;
        closenessFuncValues = new double[kernelSize][kernelSize];
        similarityFuncValues = new double[COLOR_DEPTH][COLOR_DEPTH];
    }

    GaussImageFilter(int kernelSize, double sigmaD, double sigmaR) {
        this.kernelSize = kernelSize;
        this.sigmaD = sigmaD;
        this.sigmaR = sigmaR;
        closenessFuncValues = new double[kernelSize][kernelSize];
        similarityFuncValues = new double[COLOR_DEPTH][COLOR_DEPTH];
    }

    GaussImageFilter(Hashtable<String, Object> params) {
        if (params != null && !params.isEmpty()) {
            Object kernelSizeTemp = params.get("kernelSize");
            if (kernelSizeTemp != null) {
                kernelSize = (Integer) kernelSizeTemp;
            } else {
                kernelSize = -1;
            }
            Object sigmaDTemp = params.get("sigmaD");
            if (sigmaDTemp != null) {
                sigmaD = (Integer) sigmaDTemp;
            } else {
                sigmaD = -1;
            }
            Object sigmaRTemp = params.get("sigmaR");
            if (sigmaRTemp != null) {
                sigmaR = (Integer) sigmaRTemp;
            } else {
                sigmaR = -1;
            }
        } else {
            kernelSize = -1;
            sigmaD = -1;
            sigmaR = -1;
        }
        closenessFuncValues = new double[kernelSize][kernelSize];
        similarityFuncValues = new double[COLOR_DEPTH][COLOR_DEPTH];
    }

    private void initClosenessFunc() {
        int halfKernel = kernelSize / 2;
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                double euclideanDistance = sqrt((pow((i - halfKernel), 2.0) + pow((j - halfKernel), 2.0)));
                double squarePower = pow(euclideanDistance / sigmaD, 2.0);
                closenessFuncValues[i][j] = exp(-0.5 * squarePower);
            }
        }
    }

    private void initSimilarityFunc() {
        for (int i = 0; i < COLOR_DEPTH; i++) {
            for (int j = 0; j < COLOR_DEPTH; j++) {
                double squarePower = pow(((double) abs(i - j) / sigmaR), 2.0);
                similarityFuncValues[i][j] = exp(-0.5 * squarePower);
            }
        }
    }

    public Image applyFilter(Image input) {
        Image output = input.clone();
        initClosenessFunc();
        initSimilarityFunc();
        input.stream().parallel().forEach(customColor -> {
            CustomColor[][] pointNeighbourhood = input.getPointNeighbourhood(customColor.getX(), customColor.getY(), kernelSize);
            CustomColor outputColor = applyFilterInternal(customColor, pointNeighbourhood);
            output.replaceColorValue(customColor.getX(), customColor.getY(), outputColor);
        });
        return output;
    }

    private CustomColor applyFilterInternal(CustomColor input, CustomColor[][] neighbourhoodMatrix) {
        int resultRed = applyFilterSingleColor(input.getRed(), neighbourhoodMatrix, CustomColor.Color.R);
        int resultGreen = applyFilterSingleColor(input.getGreen(), neighbourhoodMatrix, CustomColor.Color.G);
        int resultBlue = applyFilterSingleColor(input.getBlue(), neighbourhoodMatrix, CustomColor.Color.B);

        return new CustomColor(resultRed, resultGreen, resultBlue, input.getX(), input.getY());
    }

    private int applyFilterSingleColor(Integer inputColor, CustomColor[][] neighbourhoodMatrix, CustomColor.Color colorName) {
        double nominator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < neighbourhoodMatrix.length; i++) {
            for (int j = 0; j < neighbourhoodMatrix[i].length; j++) {
                int neighbourColor = neighbourhoodMatrix[i][j].getColor(colorName);

                double tempColor = closenessFuncValues[i][j] * similarityFuncValues[neighbourColor][inputColor];
                denominator += tempColor;
                nominator += neighbourColor * tempColor;
            }
        }
        return (byte) (nominator / denominator) & 0xFF;
    }
}
