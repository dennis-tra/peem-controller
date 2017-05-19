package de.agbauer.physik.FileSystem;

import ij.ImagePlus;

import java.io.IOException;
import java.util.logging.Logger;

public class ImageSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void save(ImagePlus imagePlus, String filePath) throws IOException {

        logger.info("Saving image to " + filePath);

        ij.io.FileSaver fileSaver = new ij.io.FileSaver(imagePlus);
        fileSaver.saveAsTiff(filePath);

        logger.info("Successfully saved image!");
    }
}
