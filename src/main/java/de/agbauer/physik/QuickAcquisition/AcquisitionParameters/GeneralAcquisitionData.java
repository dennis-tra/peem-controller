package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import java.io.Serializable;

public class GeneralAcquisitionData implements Serializable {
    public final String sampleName;
    public final String excitation;
    public final String aperture;
    public final String note;

    public GeneralAcquisitionData(String sampleName, String excitation, String aperture, String note) {
        this.sampleName = sampleName;
        this.excitation = excitation;
        this.aperture = aperture;
        this.note = note;
    }
}
