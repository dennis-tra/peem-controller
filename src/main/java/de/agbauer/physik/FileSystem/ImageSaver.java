package de.agbauer.physik.FileSystem;

import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import ij.ImagePlus;

import java.io.IOException;
import java.util.logging.Logger;

public class ImageSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void save(AcquisitionParameters acquisitionParameters, ImagePlus imagePlus, String filePath) throws IOException {

        logger.info("Saving image to " + filePath);

        for (PeemProperty peemProperty: PeemProperty.values()) {

            Double voltage = acquisitionParameters.peemVoltages.get(peemProperty);
            if (voltage != null) {
                imagePlus.setProperty(peemProperty.cmdString() + " U", voltage);
            }

            Double current = acquisitionParameters.peemCurrents.get(peemProperty);
            if (voltage != null) {
                imagePlus.setProperty(peemProperty.cmdString() + " I", current);
            }
        }

        imagePlus.setProperty("Excitation", acquisitionParameters.generalData.excitation);
        imagePlus.setProperty("Aperture", acquisitionParameters.generalData.aperture);

        ij.io.FileSaver fileSaver = new ij.io.FileSaver(imagePlus);
        fileSaver.saveAsTiff(filePath);

        logger.info("Successfully saved image!");
    }
}
