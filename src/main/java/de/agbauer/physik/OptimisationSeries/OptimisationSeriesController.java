package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Generic.LogManager;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.PEEMCommunicator.PEEMQuantity;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * Created by dennis on 29/01/2017.
 */
public class OptimisationSeriesController implements DocumentListener {
    private final LogManager logManager;
    private final OptimisationSeriesForm form;
    private PEEMCommunicator peemCommunicator;
    private Studio studio;
    private OptimisationSeriesExecuter optimisationSeriesExecuter;


    public OptimisationSeriesController(Studio studio, PEEMCommunicator peemCommunicator, LogManager logManager, OptimisationSeriesForm form) {
        this.logManager = logManager;
        this.form = form;
        this.studio = studio;
        this.peemCommunicator = peemCommunicator;

        this.form.startSeriesButton.addActionListener(this::startButtonClicked);
        this.form.startingValueTextField.getDocument().addDocumentListener(this);
        this.form.endingValueTextField.getDocument().addDocumentListener(this);
        this.form.stepSizeTextField.getDocument().addDocumentListener(this);
        this.form.exposureTextField.getDocument().addDocumentListener(this);

        this.form.focusRadioButton.addActionListener(this::showCurrentValue);
        this.form.stigmatorXRadioButton.addActionListener(this::showCurrentValue);
        this.form.stigmatorYRadioButton.addActionListener(this::showCurrentValue);
        this.form.extraktorRadioButton.addActionListener(this::showCurrentValue);
    }

    private void showCurrentValue(ActionEvent e) {
        PEEMProperty property = getPropertyToOptimiseSelection();
        new Thread(() -> {
            try {
                String propertyVal = peemCommunicator.getProperty(property, PEEMQuantity.VOLTAGE);
                logManager.inform("Current value: "+ property.displayName()+" = " + propertyVal + " V", true, false);
            } catch (IOException exc) {
                logManager.error("", exc, false);
            }
        }).run();
    }

    private void aTextFieldChanged(DocumentEvent e) {
        String startingValueStr = form.startingValueTextField.getText();
        String endingValueStr = form.endingValueTextField.getText();
        String stepSizeStr = form.stepSizeTextField.getText();
        String exposureValueStr = form.exposureTextField.getText();

        try {
            float startingValue = Float.parseFloat(startingValueStr);
            float endingValue = Float.parseFloat(endingValueStr);
            float stepSizeValue = Float.parseFloat(stepSizeStr);
            float exposureTimeInSeconds = Float.parseFloat(exposureValueStr);

            ArrayList<Float> values = generateMeasurementValuesFrom(startingValue, endingValue, stepSizeValue);

            double totalTimeInMinutes = (((float)values.size()) * exposureTimeInSeconds / 60.0);
            double fullMinutes = Math.floor(totalTimeInMinutes);
            double restSeconds = (totalTimeInMinutes - Math.floor(totalTimeInMinutes)) * 60;

            String totalTimeStr = String.format("%.0f:%02.0f", fullMinutes, restSeconds);

            logManager.inform("Images: " + values.size() + " - Total time: " + totalTimeStr + " min", true, false);

        } catch (NumberFormatException | ValueException exception) {
            logManager.inform("Warning: " + exception.getMessage(), true, false);
        }
    }

    private boolean isMeasuring() {
        return optimisationSeriesExecuter != null;
    }

    private void postMessageToSlackChannel(boolean successfull) {
//        OkHttpClient client = new OkHttpClient();
//
//        JSONObject payload = new JSONObject();
//
//        try {
//            payload.put("username", "PEEM");
//            payload.put("link_names", 1);
//
//            if (successfull) {
//                payload.put("text", "@channel Optimisation series finished!");
//                payload.put("icon_emoji", ":camera:");
//            } else {
//                payload.put("text", "@channel Optimisation series failed!");
//                payload.put("icon_emoji", ":red_circle:");
//            }
//
//            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), payload.toString());
//
//            Request request = new Request.Builder()
//                    .url("https://hooks.slack.com/services/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")
//                    .post(body)
//                    .build();
//
//            client.newCall(request).execute();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void startButtonClicked(ActionEvent e) {
        if (isMeasuring()) {
            optimisationSeriesExecuter.cancelSeries();
            form.setGUIToCancelledSeriesState();
        } else {

            String startingValueStr = form.startingValueTextField.getText();
            String endingValueStr = form.endingValueTextField.getText();
            String stepSizeStr = form.stepSizeTextField.getText();
            String exposureValueStr = form.exposureTextField.getText();

            boolean saveImages = form.saveSeriesCheckBox.isSelected();
            boolean sendNotification = form.sendNotificationCheckBox.isSelected();

            try {
                float startingValue = Float.parseFloat(startingValueStr);
                float endingValue = Float.parseFloat(endingValueStr);
                float stepSizeValue = Float.parseFloat(stepSizeStr);
                float exposureTimeInSeconds = Float.parseFloat(exposureValueStr);

                PEEMProperty property = getPropertyToOptimiseSelection();

                ArrayList<Float> values = generateMeasurementValuesFrom(startingValue, endingValue, stepSizeValue);


                OptimisationSeriesParameters optimisationSeriesParameters = new OptimisationSeriesParameters(values, exposureTimeInSeconds, saveImages, sendNotification, property);

                optimisationSeriesExecuter = new OptimisationSeriesExecuter(studio, peemCommunicator, logManager);

                CompletableFuture.runAsync(() -> {
                    try {
                        optimisationSeriesExecuter.startSeries(optimisationSeriesParameters);
                    } catch (Exception e1) {
                        throw new CompletionException(e1);
                    }

                    seriesEnded();
                }).exceptionally((exc) -> {
                    logManager.error("Optimisation series failed", (Exception) exc, true);
                    seriesEnded();
                    return null;
                });

                form.setGUIToMeasuringState();

            } catch (NumberFormatException | ValueException exception) {
                logManager.inform("Could not read parameters: " + exception.getMessage(), true, true);
            } catch (Exception exception) {
                logManager.error("Error in optimisation series", exception, true);
            }
        }
    }

    private void seriesEnded() {
        optimisationSeriesExecuter = null;
        form.setGUIToInputState();
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

    private PEEMProperty getPropertyToOptimiseSelection() {
        if (this.form.focusRadioButton.isSelected()) {
            return PEEMProperty.FOCUS;
        } else if (this.form.stigmatorXRadioButton.isSelected()) {
            return PEEMProperty.STIGMATOR_X;
        } else if (this.form.stigmatorYRadioButton.isSelected()) {
            return PEEMProperty.STIGMATOR_Y;
        } else if (this.form.extraktorRadioButton.isSelected()) {
            return PEEMProperty.EXTRACTOR;
        } else {
            logManager.inform("Warning: Tried to access optimisation property selection. Nothing selected.", false, true);
            return null;
        }

    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        aTextFieldChanged(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        aTextFieldChanged(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
