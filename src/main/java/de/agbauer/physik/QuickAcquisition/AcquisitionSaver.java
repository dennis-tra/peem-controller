package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;

import java.io.IOException;

public interface AcquisitionSaver {
    AcquisitionParameters save(String sampleName, CameraData cameraData) throws IOException;
}
