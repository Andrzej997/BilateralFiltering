package pl.polsl.algorithms;

import pl.polsl.domain.CustomColor;
import pl.polsl.domain.Image;

import static java.lang.Math.*;

public class GaussBilateralAlgorithm implements BilateralAlgorithm {

    private static final int COLOR_DEPTH = 256;

    private final int kernelSize; //rozmiar maski filtra
    private final double sigmaD; // współczynnik bliskości pikseli
    private final double sigmaR; //współczynnik skali podobieństwa

    private double[][] closenessFunc; //wartości funkcji bliskości pikseli
    private double[][] similarityFunc; //wartości funkcji podobieństwa piseli

    public GaussBilateralAlgorithm(int kernelSize) {
        this.kernelSize = kernelSize;
        sigmaD = 10;
        sigmaR = 300;
        closenessFunc = new double[kernelSize][kernelSize];
        similarityFunc = new double[COLOR_DEPTH][COLOR_DEPTH];
    }

    public GaussBilateralAlgorithm(int kernelSize, double sigmaD, double sigmaR) {
        this.kernelSize = kernelSize;
        this.sigmaD = sigmaD;
        this.sigmaR = sigmaR;
    }

    private void initClosenessFunc() {
        int halfKernel = kernelSize / 2;
        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                double euclideanDistance = sqrt((pow((i - halfKernel), 2.0) + pow((j - halfKernel), 2.0)));
                double squarePower = pow(euclideanDistance / sigmaD, 2.0);
                closenessFunc[i][j] = exp(-0.5 * squarePower);
            }
        }
    }

    private void initSimilarityFunc() {
        for (int i = 0; i < COLOR_DEPTH; i++) {
            for (int j = 0; j < COLOR_DEPTH; j++) {
                double squarePower = pow(((double) abs(i - j) / sigmaR), 2.0);
                similarityFunc[i][j] = exp(-0.5 * squarePower);
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
        double nominatorRed = 0.0;
        double nominatorGreen = 0.0;
        double nominatorBlue = 0.0;

        double denominatorRed = 0.0;
        double denominatorGreen = 0.0;
        double denominatorBlue = 0.0;

        for (int i = 0; i < neighbourhoodMatrix.length; i++) {
            for (int j = 0; j < neighbourhoodMatrix[i].length; j++) {
                CustomColor neighbour = neighbourhoodMatrix[i][j];
                int nRed = neighbour.getRed();
                int nGreen = neighbour.getGreen();
                int nBlue = neighbour.getBlue();

                double denR = closenessFunc[i][j] * similarityFunc[nRed][input.getRed()];
                double denG = closenessFunc[i][j] * similarityFunc[nGreen][input.getGreen()];
                double denB = closenessFunc[i][j] * similarityFunc[nBlue][input.getBlue()];

                denominatorRed += denR;
                denominatorGreen += denG;
                denominatorBlue += denB;

                nominatorRed += nRed * denR;
                nominatorGreen += nGreen * denG;
                nominatorBlue += nBlue * denB;
            }
        }


        int resultRed = (byte) (nominatorRed / denominatorRed) & 0xFF;
        int resultGreen = (byte) (nominatorGreen / denominatorGreen) & 0xFF;
        int resultBlue = (byte) (nominatorBlue / denominatorBlue) & 0xFF;

        return new CustomColor(resultRed, resultGreen, resultBlue, input.getX(), input.getY());
    }

    private CustomColor applyFilterInternal2(CustomColor input, CustomColor[][] neighbourhoodMatrix) {
        int resultRed = applyFilterSingleColor(input.getRed(), neighbourhoodMatrix, CustomColor.Color.R);
        int resultGreen = applyFilterSingleColor(input.getGreen(), neighbourhoodMatrix, CustomColor.Color.G);
        int resultBlue = applyFilterSingleColor(input.getBlue(), neighbourhoodMatrix, CustomColor.Color.B);

        return new CustomColor(resultRed, resultGreen, resultBlue, input.getX(), input.getY());
    }

    private int applyFilterSingleColor(Integer inputColor, CustomColor[][] neighbourhoodMatrix, CustomColor.Color colorName) {
        double nominator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < kernelSize; i++) {
            for (int j = 0; j < kernelSize; j++) {
                int neighbourColor = neighbourhoodMatrix[i][j].getColor(colorName);

                double tempColor = closenessFunc[i][j] * similarityFunc[neighbourColor][inputColor];
                denominator += tempColor;
                nominator += neighbourColor * tempColor;
            }
        }
        return (byte) (nominator / denominator);
    }
}