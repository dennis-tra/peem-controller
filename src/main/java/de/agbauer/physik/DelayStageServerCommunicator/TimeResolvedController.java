package de.agbauer.physik.DelayStageServerCommunicator;


import de.agbauer.physik.Observers.SampleNameChangeListener;
import jdk.nashorn.internal.runtime.regexp.joni.exception.ValueException;
import org.micromanager.Studio;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observable;
import java.util.TimeZone;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class TimeResolvedController extends Observable implements DocumentListener, SampleNameChangeListener {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private TimeResolvedSaver fileSaver;
    private final TimeResolvedForm timeResolvedForm;
    private final Studio studio;
    private final DelayStageConnectionHandler delayStageConnectionHandler;
    private DelayStageServerCommunicator delayStageServerCommunicator;
    private TimeResolvedExecuter timeResolvedExecuter;
    private String sampleName;

    public TimeResolvedController(Studio studio, DelayStageConnectionHandler delayStageConnectionHandler, TimeResolvedSaver fileSaver, TimeResolvedForm timeResolvedForm) {
        this.studio = studio;
        this.fileSaver = fileSaver;
        this.timeResolvedForm = timeResolvedForm;
        this.delayStageConnectionHandler = delayStageConnectionHandler;

        this.timeResolvedForm.startButton.addActionListener(this::startButtonClicked);
        this.timeResolvedForm.startingValueTextField.getDocument().addDocumentListener(this);
        this.timeResolvedForm.endingValueTextField.getDocument().addDocumentListener(this);
        this.timeResolvedForm.stepSizeTextField.getDocument().addDocumentListener(this);
        this.timeResolvedForm.exposureTextField.getDocument().addDocumentListener(this);

        this.timeResolvedForm.goHomeButton.addActionListener(this::goHome);
        this.timeResolvedForm.defineHomeButton.addActionListener(this::defineHome);
        this.timeResolvedForm.getPositionButton.addActionListener(this::getPosition);

        this.timeResolvedForm.stepBackButton.addActionListener(e -> this.step(false));
        this.timeResolvedForm.stepForwardButton.addActionListener(e -> this.step(true));
    }

    private void step(boolean forward) {
        try {
            DelayStageServerCommunicator communicator = getDelayStageServerCommunicator();

            float stepSize = Float.parseFloat(timeResolvedForm.manualStepSizeTextField.getText());

            if (stepSize <= 0 || stepSize > 10) {
                throw new IOException("Step size should be in range from 0 to 10 fs");
            }

            stepSize = forward ? -stepSize : stepSize;

            float position = communicator.moveRelativeBy(stepSize);

            timeResolvedForm.positionLabel.setText(String.format("%.2f fs", position));
            logger.info(String.format("Delay stage is at position %.4f fs", position));

        } catch (NumberFormatException | ValueException | IOException e) {
            String direction = forward ? "forward" : "back";
            logger.warning("Cannot step " + direction + ": " + e.getMessage());
        }
    }

    private void goHome(ActionEvent actionEvent) {
        try {
            DelayStageServerCommunicator communicator = getDelayStageServerCommunicator();

            float position = communicator.goHome();

            timeResolvedForm.positionLabel.setText(String.format("%.2f fs", position));
            logger.info(String.format("Delay stage is at position %.4f fs", position));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    private void defineHome(ActionEvent actionEvent) {
        try {
            DelayStageServerCommunicator communicator = getDelayStageServerCommunicator();

            float position = communicator.defineHome();

            timeResolvedForm.positionLabel.setText(String.format("%.2f fs", position));
            logger.info(String.format("Delay stage is at position %.4f fs", position));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    private void getPosition(ActionEvent actionEvent) {
        try {
            DelayStageServerCommunicator communicator = getDelayStageServerCommunicator();

            float position = communicator.getPosition();

            timeResolvedForm.positionLabel.setText(String.format("%.2f fs", position));
            logger.info(String.format("Delay stage is at position %.4f fs", position));
        } catch (IOException e) {
            logger.warning(e.getMessage());
        }
    }

    private DelayStageServerCommunicator getDelayStageServerCommunicator() throws IOException {
        if (delayStageServerCommunicator == null) {
            delayStageConnectionHandler.connect();

            DataOutputStream sendingStream = delayStageConnectionHandler.getSendingStream();
            BufferedReader receivingStream = delayStageConnectionHandler.getReceivingStream();

            this.delayStageServerCommunicator = new DelayStageServerCommunicator(sendingStream, receivingStream);
        }
        return delayStageServerCommunicator;
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

    private void aTextFieldChanged(DocumentEvent e) {
        try {
            TimeResolvedParameters timeResolvedParameters = getMeasurementParameters();

            double fullHours = timeResolvedParameters.getTotalFullHours();
            double restMinutes = Math.floor(timeResolvedParameters.getRestMinutes());
            double restSeconds = Math.floor(timeResolvedParameters.getRestSeconds());
            int imageCount = timeResolvedParameters.values.size();

            String totalTimeStr = String.format("%.0f:%02.0f:%02.0f", fullHours, restMinutes, restSeconds);

            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
            Date duration = format.parse(totalTimeStr);
            // + 1 is a time zone fix I suppose...
            Date newDate = new Date(new Date().getTime() + duration.getTime() + 3600000);


            DateFormat dateFormat = new SimpleDateFormat("HH:mm/dd.MM.yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            String finishTime = dateFormat.format(newDate);

            logger.info("Images: " + imageCount + " - Total time: " + totalTimeStr + " - Finished by " + finishTime);

        } catch (ValueException | ParseException exception) {
            logger.warning(exception.getMessage());
        } catch (NumberFormatException exception) { }
    }

    private TimeResolvedParameters getMeasurementParameters() {
        return new TimeResolvedParameters(
                timeResolvedForm.startingValueTextField.getText(),
                timeResolvedForm.endingValueTextField.getText(),
                timeResolvedForm.stepSizeTextField.getText(),
                timeResolvedForm.exposureTextField.getText(),
                timeResolvedForm.sendNotificationCheckBox.isSelected()
        );
    }

    private boolean isMeasuring() {
        return timeResolvedExecuter != null;
    }

    private void startButtonClicked(ActionEvent e) {
        if (isMeasuring()) {
            stopMeasuring();
        } else {
            startMeasuring();
        }
    }

    private void stopMeasuring() {
        timeResolvedExecuter.cancelSeries();
        timeResolvedForm.setGUIToCancelledSeriesState();
    }

    private void startMeasuring() {
        notifyObservers("started-acquisition");

        try {
            TimeResolvedParameters timeResolvedParameters = getMeasurementParameters();

            timeResolvedExecuter = new TimeResolvedExecuter(studio, getDelayStageServerCommunicator(), fileSaver);

            CompletableFuture.runAsync(() -> {
                try {
                    timeResolvedExecuter.startSeries(sampleName, timeResolvedParameters);
                } catch (Exception e1) {
                    throw new CompletionException(e1);
                }

                measurementEnded();
            }).exceptionally((exc) -> {
                measurementEnded();

                logger.severe("Slack: Time resolved measurement failed: " + exc.getMessage());
                return null;
            });

            timeResolvedForm.setGUIToMeasuringState();

        } catch (NumberFormatException | ValueException exception) {
            logger.info("Could not read parameters: " + exception.getMessage());
            measurementEnded();

        } catch (Exception exception) {
            logger.severe("Slack: Error in time resolved measurement: "+ exception.getMessage());
            measurementEnded();
        }
    }


    private void measurementEnded() {
        logger.info("Time resolved measurement ended");

        notifyObservers("finished-acquisition");
        timeResolvedExecuter = null;
        timeResolvedForm.setGUIToInputState();
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        this.timeResolvedForm.setEnabledState(!empty(sampleName));
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
