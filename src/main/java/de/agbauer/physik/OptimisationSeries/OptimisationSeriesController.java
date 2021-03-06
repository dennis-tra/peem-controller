package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.PeemCommunicator.PeemQuantity;
import ij.ImagePlus;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.micromanager.Studio;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class OptimisationSeriesController extends Observable implements DocumentListener, SampleNameChangeListener {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private OptimisationSeriesSaver fileSaver;
    private OptimisationSeriesForm form;
    private PeemCommunicator peemCommunicator;
    private Studio studio;
    private OptimisationSeriesExecuter optimisationSeriesExecuter;
    private String sampleName;


    public OptimisationSeriesController(Studio studio, PeemCommunicator peemCommunicator, OptimisationSeriesSaver fileSaver, OptimisationSeriesForm form) {
        this.fileSaver = fileSaver;
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
        PeemProperty property = getPropertyToOptimiseSelection();
        new Thread(() -> {
            try {
                String propertyVal = peemCommunicator.getProperty(property, PeemQuantity.VOLTAGE);
                logger.info("Current value: "+ property.displayName()+" = " + propertyVal + " V");
            } catch (IOException exc) {
                logger.severe(exc.getMessage());
            }
        }).run();
    }

    private void aTextFieldChanged(DocumentEvent e) {
        try {
            OptimisationSeriesParameters optimisationSeriesParameters = getCurrentOptimisationParameters();

            double fullMinutes = optimisationSeriesParameters.getTotalFullMinutes();
            double restSeconds = optimisationSeriesParameters.getRestSeconds();
            int imageCount = optimisationSeriesParameters.values.size();

            String totalTimeStr = String.format("%.0f:%02.0f", fullMinutes, restSeconds);

            logger.info("Images: " + imageCount + " - Total time: " + totalTimeStr + " min");

        } catch (ValueException exception) {
            logger.warning(exception.getMessage());
        } catch (NumberFormatException exc) {

        }
    }

    private boolean isMeasuring() {
        return optimisationSeriesExecuter != null;
    }

    private void startButtonClicked(ActionEvent e) {
        if (isMeasuring()) {
            stopMeasuring();
        } else {
            startMeasuring();
        }
    }

    private void stopMeasuring() {
        optimisationSeriesExecuter.cancelSeries();
        form.setGUIToCancelledSeriesState();
    }

    private void startMeasuring() {
        notifyObservers("started-acquisition");

        try {
            OptimisationSeriesParameters optimisationSeriesParameters = getCurrentOptimisationParameters();

            optimisationSeriesExecuter = new OptimisationSeriesExecuter(studio, peemCommunicator);

            CompletableFuture.runAsync(() -> {
                try {
                    List<ImagePlus> images = optimisationSeriesExecuter.startSeries(optimisationSeriesParameters);

                    if (this.form.saveSeriesCheckBox.isSelected()) {
                        fileSaver.save(sampleName, optimisationSeriesParameters, images);
                    }

                } catch (Exception e1) {
                    throw new CompletionException(e1);
                }

                seriesEnded();
            }).exceptionally((exc) -> {
                seriesEnded();

                logger.severe("Slack: Optimisation series failed: " + exc.getMessage());
                return null;
            });

            form.setGUIToMeasuringState();

        } catch (NumberFormatException | ValueException exception) {
            logger.info("Could not read parameters: " + exception.getMessage());
            seriesEnded();

        } catch (Exception exception) {
            logger.severe("Slack: Error in optimisation series: "+ exception.getMessage());
            seriesEnded();
        }
    }


    private void seriesEnded() {
        logger.info("Optimisation series ended");

        notifyObservers("finished-acquisition");
        optimisationSeriesExecuter = null;
        form.setGUIToInputState();
    }

    private OptimisationSeriesParameters getCurrentOptimisationParameters() {
        return new OptimisationSeriesParameters(
                form.startingValueTextField.getText(),
                form.endingValueTextField.getText(),
                form.stepSizeTextField.getText(),
                form.exposureTextField.getText(),
                form.saveSeriesCheckBox.isSelected(),
                form.sendNotificationCheckBox.isSelected(),
                getPropertyToOptimiseSelection()
        );
    }

    private PeemProperty getPropertyToOptimiseSelection() {
        if (this.form.focusRadioButton.isSelected()) {
            return PeemProperty.FOCUS;
        } else if (this.form.stigmatorXRadioButton.isSelected()) {
            return PeemProperty.STIGMATOR_X;
        } else if (this.form.stigmatorYRadioButton.isSelected()) {
            return PeemProperty.STIGMATOR_Y;
        } else if (this.form.extraktorRadioButton.isSelected()) {
            return PeemProperty.EXTRACTOR;
        } else {
            logger.warning("Tried to access optimisation property selection. Nothing selected.");
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

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        this.form.setEnabledState(!empty(sampleName));
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }
}
