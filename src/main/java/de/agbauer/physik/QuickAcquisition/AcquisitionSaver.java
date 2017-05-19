package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;

import java.io.IOException;

public interface AcquisitionSaver {
    void save(String sampleName, CameraData cameraData) throws IOException;
}
