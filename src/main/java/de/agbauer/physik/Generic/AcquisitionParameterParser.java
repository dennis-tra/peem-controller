package de.agbauer.physik.Generic;


import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import java.io.File;

public interface AcquisitionParameterParser {
    AcquisitionParameters parse(File file) throws Exception;
}
