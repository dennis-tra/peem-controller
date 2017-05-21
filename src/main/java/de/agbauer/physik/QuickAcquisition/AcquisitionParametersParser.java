package de.agbauer.physik.QuickAcquisition;


import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.File;
import java.io.IOException;

public interface AcquisitionParametersParser {
    AcquisitionParameters parse(File file) throws IOException;
}
