package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.PeemCommunicator.PeemProperty;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;

/**
 * Created by dennis on 29/01/2017.
 */
public class OptimisationSeriesParameters {
    ArrayList<Float> values;
    boolean saveImages;
    boolean sendNotification;
    PeemProperty property;
    float startingValue;
    float endingValue;
    float stepSizeValue;
    float exposureTimeInSeconds;

    OptimisationSeriesParameters(String startingValueStr, String endingValueStr, String stepSizeStr, String exposureValueStr, boolean saveImages, boolean sendNotification, PeemProperty property) throws NumberFormatException, ValueException {
        this.startingValue = Float.parseFloat(startingValueStr);
        this.endingValue = Float.parseFloat(endingValueStr);
        this.stepSizeValue = Float.parseFloat(stepSizeStr);
        this.exposureTimeInSeconds = Float.parseFloat(exposureValueStr);
        this.saveImages = saveImages;
        this.sendNotification = sendNotification;
        this.property = property;

        values = generateMeasurementValuesFrom(startingValue, endingValue, stepSizeValue);
    }

    private double getTotalTimeInMinutes() {
        return (((float)values.size()) * exposureTimeInSeconds / 60.0);
    }

    double getTotalFullMinutes() {
        return Math.floor(getTotalTimeInMinutes());
    }

    double getRestSeconds() {
        return (getTotalTimeInMinutes() - Math.floor(getTotalTimeInMinutes())) * 60;
    }

    private ArrayList<Float> generateMeasurementValuesFrom(float startingValue, float endingValue, float stepSizeValue) throws ValueException {

        if (stepSizeValue <= 0) {
            throw new ValueException("Step size can't be less than zero");
        }

        if (endingValue < startingValue) {
            throw new ValueException("Ending value can't be less than starting value");
        }

        if ((endingValue - startingValue) <= stepSizeValue) {
            throw new ValueException("Step size can't be bigger than measurement interval");
        }

        ArrayList<Float> measurementValues = new ArrayList<>();
        for (float value = startingValue; value < endingValue; value += stepSizeValue) {
            measurementValues.add(value);
        }
        measurementValues.add(endingValue);

        return measurementValues;
    }

    @Override
    public String toString() {
        return "Sweeping " + property.displayName() + " from " + startingValue + " V to " + endingValue +" V with " + exposureTimeInSeconds + " s exposure (" + values.size() + " images).";
    }
}

