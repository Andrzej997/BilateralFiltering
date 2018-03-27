package pl.polsl;


import org.apache.commons.configuration.ConfigurationException;
import pl.polsl.resource.loader.ResourceLoader;
import pl.polsl.resource.loader.ResourceLoaderImpl;

import java.io.IOException;
import java.net.URISyntaxException;

public class BilateralFilteringMain {

    public static void main(String[] args) throws IOException, ConfigurationException, URISyntaxException {

        ResourceLoader resourceLoader = new ResourceLoaderImpl();
        ExperimentFacade facade = new BilateralExperimentImpl(resourceLoader);
        facade.performExperiment();
    }
}
