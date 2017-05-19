package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.CameraData;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.micromanager.SnapLiveManager;
import org.micromanager.Studio;
import org.micromanager.data.Image;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class QuickAcquisitionController extends Observable implements SampleNameChangeListener {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private QuickAcquisitionForm form;
    private Studio studio;
    private SnapLiveManager snapLiveManager;
    private String sampleName;

    private interface AcquisitionAction {
        void acquire(float exposureInMs, int binning);
    }

    public QuickAcquisitionController(Studio studio, AcquisitionSaver fileSaver, QuickAcquisitionForm form) {
        this.form = form;
        this.studio = studio;
        this.snapLiveManager = studio == null ? null : studio.getSnapLiveManager();

        this.form.stopButton.addActionListener(e -> turnOffLiveMode());

        this.form.liveButton.addActionListener(e -> acquireImage(form.liveTextField, form.liveComboBox, (exposureInMs, binning) -> {
            snapLiveManager.setLiveMode(true);
            logger.info("Started live mode... ");
        }));

        this.form.snapButton.addActionListener(e -> acquireImage(form.snapTextField, form.snapComboBox, (exposureInMs, binning) -> {
            Image image = snapLiveManager.snap(true).get(0);

            // Post to slack if exposure is longer than three minutes
            logger.info((exposureInMs >= 180000 ? "Slack: @channel " : "") + "Successfully snapped image!");

            ImageProcessor ip = studio.data().getImageJConverter().createProcessor(image);
            ImagePlus imagePlus = new ImagePlus(sampleName, ip);

            CameraData cameraData = new CameraData(imagePlus, exposureInMs, binning);

            try {
                fileSaver.save(sampleName, cameraData);

                studio.getAlbum().addImage(image);

            } catch (IOException exc) {

                logger.warning("Failed saving acquisition: " + exc.getMessage());

            } finally {
                setChanged();
                notifyObservers(imagePlus);
            }
        }));

    }

    private void acquireImage(JTextField exposureTextField, JComboBox binningComboBox, AcquisitionAction acquisitionAction) {
        logger.info("Starting image acquisition...");

        try {
            turnOffLiveMode();

            float exposureInMs = Float.parseFloat(exposureTextField.getText());
            int binning = Integer.parseInt((String) binningComboBox.getSelectedItem());

            logger.info("Settting camera exposure to " + exposureInMs/1000 + " s");
            studio.getCMMCore().setExposure(Constants.cameraDevice, exposureInMs);

            logger.info("Setting camera binning to " + binning);
            studio.getCMMCore().setProperty(Constants.cameraDevice, "Binning", binning);

            if (snapLiveManager.getDisplay() != null) {
                double zoomFactor = zoomForBinning(binning);
                logger.info("Setting display window zoom factor to " + Math.round(zoomFactor * 100) + " %");
                snapLiveManager.getDisplay().setZoom(zoomFactor);
            }

            acquisitionAction.acquire(exposureInMs, binning);

        } catch(NumberFormatException exc) {
            logger.warning("Couldn't read your quick acquisition input values: " + exc.getMessage());
        } catch (Exception e1) {
            logger.severe("Couldn't snap an image: " + e1.getMessage());
        }
    }

    private void turnOffLiveMode() {
        logger.info("Stopping live acquisition");

        if (snapLiveManager.getIsLiveModeOn()) {
            snapLiveManager.setLiveMode(false);
        }
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        this.form.setGeneralInformationGiven(!empty(this.sampleName));
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

    private double zoomForBinning(int binning) {
        switch (binning) {
            case 1:
                return 0.75;
            case 2:
                return 1.5;
            case 4:
                return 3.0;
            case 8:
                return 6.0;
            default:
                return 1.0;
        }
    }
}
