package de.agbauer.physik.QuickAcquisition;


import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.File;

public interface AcquisitionParameterParser {
    AcquisitionParameters parse(File file) throws Exception;
}
