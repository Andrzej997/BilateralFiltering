package pl.polsl;

import pl.polsl.algorithms.BilateralAlgorithm;
import pl.polsl.algorithms.GaussBilateralAlgorithm;
import pl.polsl.domain.Image;
import pl.polsl.domain.ImageFactory;
import pl.polsl.domain.ImageTypesEnum;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class BilateralFilteringMain {

    public static void main(String[] args) throws IOException, URISyntaxException {
        URL resource = BilateralFilteringMain.class.getClassLoader().getResource("images/grey/baltic_sea.jpg");
        assert resource != null;
        URI uri = resource.toURI();
        File file = new File(uri);
        Image img = new ImageFactory().getImageFromFile(ImageTypesEnum.BASIC, file);
        BilateralAlgorithm algorithm = new GaussBilateralAlgorithm(15);
        Image result = algorithm.applyFilter(img);
        result.toFile("E:\\img\\result.jpg");
    }
}
