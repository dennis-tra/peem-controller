package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.logging.Logger;

public class AcquisitionParametersSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private AcquisitionParametersFormatter formatter;

    AcquisitionParametersSaver(AcquisitionParametersFormatter formatter) {
        this.formatter = formatter;
    }

    void save(AcquisitionParameters ap, String filePath) throws IOException {

        logger.info("Saving peem params to " + filePath);

        String paramsString = formatter.format(ap);

        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            out.print(paramsString);
        }

        logger.info("Successfully saved!");
    }
}
