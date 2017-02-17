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

    public QuickAcquisitionController(Studio studio, FileSaver fileSaver, QuickAcquisitionForm form) {
        this.fileSaver = fileSaver;
        this.form = form;
        this.studio = studio;
        this.snapLiveManager = studio == null ? null : studio.getSnapLiveManager();

        this.form.stopButton.addActionListener(e -> turnOffLiveMode());

        this.form.liveButton.addActionListener(e -> acquireImage(form.liveTextField, form.liveComboBox, new LiveAcquisition()));
        this.form.snapButton.addActionListener(e -> acquireImage(form.snapTextField, form.snapComboBox, new SnapAcquisition()));
        this.form.snapPlusButton.addActionListener(e -> acquireImage(form.snapPlusTextField, form.snapPlusComboBox, new SnapPlusAcquisition()));

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

    private interface AcquisitionAction {
        void acquire(float exposureInMs, int binning);
    }

    private class SnapAcquisition implements AcquisitionAction {

        @Override
        public void acquire(float exposureInMs, int binning) {
            logger.info("Snapping image with t = " + exposureInMs/1000 +" s and binning " + binning);
            snapLiveManager.snap(true);
            logger.info("Successfully snapped image!");
        }
    }

    private class LiveAcquisition implements AcquisitionAction {

        @Override
        public void acquire(float exposureInMs, int binning) {
            snapLiveManager.setLiveMode(true);
            logger.info("Started live mode... ");
        }
    }

    private class SnapPlusAcquisition implements AcquisitionAction {

        @Override
        public void acquire(float exposureInMs, int binning) {

            logger.info("Snapping image with t = " + exposureInMs/1000 +" s and binning " + binning);
            snapLiveManager.snap(true).get(0);
            logger.info("Successfully snapped image!");

            ImagePlus imagePlus = snapLiveManager.getDisplay().getImagePlus();

            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save this image?", "Save image dialog", JOptionPane.YES_NO_OPTION);

            if(dialogResult == JOptionPane.YES_OPTION) {

                try {
                    fileSaver.save(generalInformationData, imagePlus, "" + exposureInMs);
                } catch (IOException exc) {
                    JOptionPane.showMessageDialog(null, "Failed saving acquisition: " + exc.getMessage(), "Failed saving", JOptionPane.OK_OPTION);
                }

                setChanged();
                notifyObservers(imagePlus);

            } else {
                logger.info("User denied saving image");
            }
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
