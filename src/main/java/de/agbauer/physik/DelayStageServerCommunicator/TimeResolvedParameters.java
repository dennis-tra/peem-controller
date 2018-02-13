package de.agbauer.physik.DelayStageServerCommunicator;

import de.agbauer.physik.PeemCommunicator.PeemProperty;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;

import java.util.ArrayList;

public class TimeResolvedParameters {

    ArrayList<Double> values;
    boolean sendNotification;
    float startingValue;
    float endingValue;
    float stepSizeValue;
    float exposureTimeInSeconds;

    TimeResolvedParameters(String startingValueStr, String endingValueStr, String stepSizeStr, String exposureValueStr, boolean sendNotification) throws NumberFormatException, ValueException {
        this.startingValue = Float.parseFloat(startingValueStr);
        this.endingValue = Float.parseFloat(endingValueStr);
        this.stepSizeValue = Float.parseFloat(stepSizeStr);
        this.exposureTimeInSeconds = Float.parseFloat(exposureValueStr);
        this.sendNotification = sendNotification;

        values = generateMeasurementValuesFrom(startingValue, endingValue, stepSizeValue);
    }

    private double getTotalTimeInMinutes() {
        return (((float)values.size()) * exposureTimeInSeconds / 60.0);
    }

    private double getTotalTimeInHours() {
        return (((float)values.size()) * exposureTimeInSeconds / 60.0 / 60.0);
    }

    double getTotalFullHours() {
        return Math.floor(getTotalTimeInHours());
    }

    double getRestMinutes() {
        return (getTotalTimeInHours() - Math.floor(getTotalTimeInHours())) * 60;
    }

    double getRestSeconds() { return (getTotalTimeInMinutes() - Math.floor(getTotalTimeInMinutes())) * 60;
    }

    private ArrayList<Double> generateMeasurementValuesFrom(double startingValue, double endingValue, double stepSizeValue) throws ValueException {

        if (endingValue < startingValue && stepSizeValue >= 0) {
            throw new ValueException("Ending value < Starting Value -> step size can't be less than zero");
        }

        if (endingValue > startingValue && stepSizeValue <= 0) {
            throw new ValueException("Ending value > Starting Value -> step size can't be bigger than zero");
        }

        if (Math.abs(endingValue - startingValue) <= Math.abs(stepSizeValue)) {
            throw new ValueException("Step size can't be bigger than measurement interval");
        }

        ArrayList<Double> measurementValues = new ArrayList<>();
        for (double value = startingValue; value < endingValue; value += stepSizeValue) {
            measurementValues.add(value);
        }
        measurementValues.add(endingValue);

        return measurementValues;
    }

    @Override
    public String toString() {
        return "Time resolve measurement from " + startingValue + " fs to " + endingValue +" fs with " + exposureTimeInSeconds + " s exposure (" + values.size() + " images).";
    }
}

