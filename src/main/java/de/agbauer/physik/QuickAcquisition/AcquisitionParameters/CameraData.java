package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import ij.ImagePlus;

public class CameraData {
    public final ImagePlus imagePlus;
    public final float exposureInMs;
    public final int binning;

    public CameraData(ImagePlus imagePlus, float exposureInMs, int binning) {
        this.imagePlus = imagePlus;
        this.exposureInMs = exposureInMs;
        this.binning = binning;
    }
}
