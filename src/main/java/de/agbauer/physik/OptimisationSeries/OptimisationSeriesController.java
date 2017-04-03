package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Observers.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.PEEMCommunicator.PEEMQuantity;
import ij.ImagePlus;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.micromanager.Studio;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class OptimisationSeriesController extends Observable implements DocumentListener, GeneralInformationChangeListener {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private OptimisationSeriesForm form;
    private PEEMCommunicator peemCommunicator;
    private Studio studio;
    private OptimisationSeriesExecuter optimisationSeriesExecuter;
    private GeneralInformationData generalInformationData;


    public OptimisationSeriesController(Studio studio, PEEMCommunicator peemCommunicator, OptimisationSeriesForm form) {
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

//                    if (optimisationSeriesParameters.saveImages) {
//                        FileSaver fs = new FileSaver(peemCommunicator);
//                        fs.saveOptimisationSeries(generalInformationData, images);
//                    }

                } catch (Exception e1) {
                    throw new CompletionException(e1);
                }

                seriesEnded();
            }).exceptionally((exc) -> {
                seriesEnded();

                logger.severe("Optimisation series failed: " + exc.getMessage());
                return null;
            });

            form.setGUIToMeasuringState();

        } catch (NumberFormatException | ValueException exception) {
            logger.info("Could not read parameters: " + exception.getMessage());
            seriesEnded();

        } catch (Exception exception) {
            logger.severe("Error in optimisation series: "+ exception.getMessage());
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
    public void generalInformationChanged(GeneralInformationData data) {
        this.generalInformationData = data;
        this.form.setEnabledState(data.isValid());
    }

}
