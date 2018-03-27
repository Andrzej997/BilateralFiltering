package pl.polsl;

import org.apache.commons.configuration.ConfigurationException;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ExperimentFacade {

    void performExperiment() throws ConfigurationException, IOException, URISyntaxException;
}
