package de.agbauer.physik.FileSystem;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

public interface DataFiler {
    void setAcquisitionParams(AcquisitionParameters ap);
    String getWorkingDirectory();
    String getScopeName();
    String getTifImageFilePath();
    String getPeemParametersFilePath();
    int getImageNumber();
}
