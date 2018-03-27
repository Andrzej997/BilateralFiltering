package pl.polsl;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import pl.polsl.algorithms.ImageFilter;
import pl.polsl.algorithms.ImageFilterFactory;
import pl.polsl.algorithms.ImageFilterType;
import pl.polsl.domain.Image;
import pl.polsl.domain.ImageFactory;
import pl.polsl.domain.ImageTypesEnum;
import pl.polsl.resource.loader.ResourceLoader;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class BilateralExperimentImpl implements ExperimentFacade {

    private String imageType;
    private ImageTypesEnum imageTypeEnum;
    private List<Integer> kernelSizes;
    private List<Integer> sigmaRValues;
    private List<Integer> sigmaDValues;
    private ResourceLoader resourceLoader;
    private String[] imageAlgorithms;
    private String resultFolder;

    public BilateralExperimentImpl(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private void loadProperties() throws ConfigurationException {
        PropertiesConfiguration config = new PropertiesConfiguration();
        URL resource = this.getClass().getClassLoader().getResource("application.properties");
        assert resource != null;
        config.load(resource);
        config.setListDelimiter(',');
        imageType = config.getString("imageType");
        List kernelSizesString = config.getList("kernelSizes");
        if (kernelSizesString != null && !kernelSizesString.isEmpty()) {
            kernelSizes = (List) kernelSizesString.stream().map(o -> Integer.parseInt((String) o)).collect(Collectors.toList());
        }
        List sigmaRValuesString = config.getList("sigmaRValues");
        if (sigmaRValuesString != null && !sigmaRValuesString.isEmpty()) {
            sigmaRValues = (List) sigmaRValuesString.stream().map(o -> Integer.parseInt((String) o)).collect(Collectors.toList());
        }
        List sigmaDValuesString = config.getList("sigmaDValues");
        if (sigmaDValuesString != null && !sigmaDValuesString.isEmpty()) {
            sigmaDValues = (List) sigmaDValuesString.stream().map(o -> Integer.parseInt((String) o)).collect(Collectors.toList());
        }
        String imageTypesName = config.getString("domainImageType");
        imageTypeEnum = ImageTypesEnum.get(imageTypesName);
        imageAlgorithms = config.getStringArray("imageAlgorithms");
        resultFolder = config.getString("resultFolder");
    }

    private List<File> loadExperimentFiles() throws IOException, URISyntaxException {
        if (StringUtils.isEmpty(imageType)) {
            return null;
        }
        String imagesPath = "images/" + imageType + "/";
        return resourceLoader.loadAllFolderResources(imagesPath);
    }

    @Override
    public void performExperiment() throws ConfigurationException, IOException, URISyntaxException {
        loadProperties();
        List<File> experimentFiles = loadExperimentFiles();
        if (experimentFiles == null || experimentFiles.isEmpty()) {
            throw new ConfigurationException("No entry files loaded");
        }
        experimentFiles.parallelStream().forEach(this::performExperimentOnFile);
    }

    private void performExperimentOnFile(File imageFile) {
        Image img;
        try {
            img = new ImageFactory().getImageFromFile(imageTypeEnum, imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Image does not exists");
        }
        if (imageAlgorithms == null) {
            throw new RuntimeException("No image algorithms");
        }
        kernelSizes.forEach(kernelSize -> sigmaRValues.forEach(sigmaR -> sigmaDValues.forEach(sigmaD -> {
            Hashtable<String, Object> properties = new Hashtable<>();
            properties.put("kernelSize", kernelSize);
            properties.put("sigmaR", sigmaR);
            properties.put("sigmaD", sigmaD);
            for (String imageAlgorithm : imageAlgorithms) {
                ImageFilterType imageFilterType = ImageFilterType.get(imageAlgorithm);
                ImageFilter filter = new ImageFilterFactory().provideFilter(imageFilterType, properties);
                Image result = filter.applyFilter(img);
                try {
                    result.toFile(getOutputFilename(imageFile.getName(), imageAlgorithm, kernelSize, sigmaR, sigmaD, result.getFormat()));
                } catch (IOException e) {
                    throw new RuntimeException("toFile error");
                }
            }
        })));
    }

    private String getOutputFilename(String baseName, String method, Integer kernelSize, Integer sigmaR, Integer sigmaD, String format) {
        return resultFolder + method + "\\" + baseName + "_" + +kernelSize + "_" + sigmaR + "_" + sigmaD + "." + format;
    }
}