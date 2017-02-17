package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.GeneralInformation.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.Generic.Constants;
import ij.ImagePlus;
import org.micromanager.SnapLiveManager;
import org.micromanager.Studio;

import javax.swing.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class QuickAcquisitionController extends Observable implements GeneralInformationChangeListener {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private FileSaver fileSaver;
    private QuickAcquisitionForm form;
    private Studio studio;
    private SnapLiveManager snapLiveManager;
    private GeneralInformationData generalInformationData;

    private interface AcquisitionAction {
        void acquire(float exposureInMs, int binning);
    }

    public QuickAcquisitionController(Studio studio, FileSaver fileSaver, QuickAcquisitionForm form) {
        this.fileSaver = fileSaver;
        this.form = form;
        this.studio = studio;
        this.snapLiveManager = studio == null ? null : studio.getSnapLiveManager();

        this.form.stopButton.addActionListener(e -> turnOffLiveMode());

        this.form.liveButton.addActionListener(e -> acquireImage(form.liveTextField, form.liveComboBox, (exposureInMs, binning) -> {
            snapLiveManager.setLiveMode(true);
            logger.info("Started live mode... ");
        }));

        this.form.snapButton.addActionListener(e -> acquireImage(form.snapTextField, form.snapComboBox, (exposureInMs, binning) -> {
            snapLiveManager.snap(true);
            logger.info("Successfully snapped image!");
        }));

        this.form.snapPlusButton.addActionListener(e -> acquireImage(form.snapPlusTextField, form.snapPlusComboBox, (exposureInMs, binning) -> {
            snapLiveManager.snap(true).get(0);
            logger.info("Successfully snapped image!");

            ImagePlus imagePlus = snapLiveManager.getDisplay().getImagePlus();
            askToSaveImage(imagePlus, exposureInMs);
        }));

    }


    private void acquireImage(JTextField exposureTextField, JComboBox binningComboBox, AcquisitionAction acquisitionAction) {
        logger.info("Starting image acquisition...");

        try {
            turnOffLiveMode();

            float exposureInMs = Float.parseFloat(exposureTextField.getText());
            int binning = Integer.parseInt((String) binningComboBox.getSelectedItem());

            logger.info("Settting camera exposure to " + exposureInMs/1000 + " s");
            studio.getCMMCore().setExposure(exposureInMs);

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

    private void askToSaveImage(ImagePlus imagePlus, double exposureInMs) {

        int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save this image?", "Save image dialog", JOptionPane.YES_NO_OPTION);

        if(dialogResult != JOptionPane.YES_OPTION) {
            logger.info("User denied saving image");
            return;
        }

        try {
            fileSaver.save(generalInformationData, imagePlus, "" + exposureInMs);
        } catch (IOException exc) {
            JOptionPane.showMessageDialog(null, "Failed saving acquisition: " + exc.getMessage(), "Failed saving", JOptionPane.OK_OPTION);
        } finally {
            setChanged();
            notifyObservers(imagePlus);
        }
    }

    private void turnOffLiveMode() {
        logger.info("Stopping live acquisition");

        if (snapLiveManager.getIsLiveModeOn()) {
            snapLiveManager.setLiveMode(false);
        }
    }

    @Override
    public void generalInformationChanged(GeneralInformationData data) {
        this.generalInformationData = data;
        this.form.setGeneralInformationGiven(data.isValid());
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
