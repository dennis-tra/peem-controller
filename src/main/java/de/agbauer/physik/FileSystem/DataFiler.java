package de.agbauer.physik.FileSystem;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.File;

public interface DataFiler {
    FileLocations setAcquisitionParams(AcquisitionParameters ap);
    String getWorkingDirectoryFor(String sampleName);
    String generateScopeName(String sampleName);
    int calculateImageNumber(String sampleName);
    boolean isParamsTextFile(File file);
}
