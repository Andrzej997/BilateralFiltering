package pl.polsl.algorithms;

import pl.polsl.domain.Image;

public interface ImageFilter {
    Image applyFilter(Image input);
}
