package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import java.util.ArrayList;

/**
 * Created by dennis on 29/01/2017.
 */
public class OptimisationSeriesParameters {
    ArrayList<Float> values;
    float exposureTimeInS;
    boolean saveImages;
    boolean sendNotification;
    PEEMProperty property;

    OptimisationSeriesParameters(ArrayList<Float> values, float exposureTimeInS, boolean saveImages, boolean sendNotification, PEEMProperty property) {
        this.values = values;
        this.exposureTimeInS = exposureTimeInS;
        this.saveImages = saveImages;
        this.sendNotification = sendNotification;
        this.property = property;
    }

}

