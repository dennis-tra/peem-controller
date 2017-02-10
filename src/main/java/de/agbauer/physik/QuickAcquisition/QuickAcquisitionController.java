package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.PersistenceHandler;
import ij.ImagePlus;
import org.micromanager.SnapLiveManager;
import org.micromanager.Studio;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.logging.Logger;

public class QuickAcquisitionController extends Observable {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final QuickAcquisitionForm form;
    private Studio studio;
    private PersistenceHandler persistenceHandler;
    private SnapLiveManager snapLiveManager;

    public QuickAcquisitionController(Studio studio, PersistenceHandler persistenceHandler, QuickAcquisitionForm form) {
        this.form = form;
        this.studio = studio;
        this.persistenceHandler = persistenceHandler;
        this.snapLiveManager = studio.getSnapLiveManager();

        this.form.liveButton.addActionListener(this::liveMode);
        this.form.snapButton.addActionListener(this::snapImage);
        this.form.snapPlusButton.addActionListener(this::snapPlusImage);
        this.form.stopButton.addActionListener(e -> turnOffLiveMode());
    }

    private void snapPlusImage(ActionEvent actionEvent) {
        if (!persistenceHandler.isAllInformationAvailable()) {
            logger.info("Error: Please enter a name and excitation necessary information");
            return;
        }

        logger.info("Acquiring image...");

        notifyObservers("started-acquisition");

        turnOffLiveMode();

        String exposureStr = this.form.snapPlusTextField.getText();

        float exposureInMs = Float.parseFloat(exposureStr);
        int binning = Integer.parseInt((String) this.form.snapPlusComboBox.getSelectedItem());

        try {
            setExposure(exposureInMs);
            setCameraBinning(binning);

//            PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);
//
//            Map<PEEMProperty, String> peemVoltages = bulkReader.getAllVoltages();
//            Map<PEEMProperty, String> peemCurrents = bulkReader.getAllCurrents();

            snapLiveManager.snap(true).get(0);
            ImagePlus imagePlus = snapLiveManager.getDisplay().getImagePlus();

            int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save this image?", "Save image dialog", JOptionPane.YES_NO_OPTION);

            if(dialogResult == JOptionPane.YES_OPTION) {
                logger.info("Saving image...");

                notifyObservers(imagePlus);
            } else {
                logger.info("User denied saving image");
            }

        } catch (Exception e1) {
            logger.severe("Couldn't snap an image: " + e1.getMessage());
        } finally {
            notifyObservers("finished-acquisition");
        }
    }

    private void snapImage(ActionEvent actionEvent) {
        logger.info("Acquiring image...");
        setChanged();
        notifyObservers("started-acquisition");

        turnOffLiveMode();

        String exposureStr = this.form.snapTextField.getText();

        float exposureInMs = Float.parseFloat(exposureStr);
        int binning = Integer.parseInt((String) this.form.snapComboBox.getSelectedItem());

        try {
            setExposure(exposureInMs);
            setCameraBinning(binning);
            snapLiveManager.snap(true);

        } catch(NumberFormatException exc) {
            logger.warning("Couldn't read your quick acquisition input values");
        } catch (Exception e1) {
            logger.severe("Couldn't snap an image : " + e1.getMessage());
        } finally {
            setChanged();
            notifyObservers("finished-acquisition");
        }
    }

    private void liveMode(ActionEvent e) {
        logger.info("Turning on Live mode");

        turnOffLiveMode();

        try {
            float exposureInMs = Float.parseFloat(this.form.liveTextField.getText());
            int binning = Integer.parseInt((String) this.form.liveComboBox.getSelectedItem());

            setExposure(exposureInMs);
            setCameraBinning(binning);
            snapLiveManager.setLiveMode(true);

        } catch(NumberFormatException exc) {
            logger.warning("Couldn't read your quick acquisition input values");
        } catch (Exception e1) {
            logger.severe("Couldn't turn live mode on : " + e1.getMessage());
        }
    }

    private void turnOffLiveMode() {
        logger.info("Stopping live acquisition");

        if (snapLiveManager.getIsLiveModeOn()) {
            snapLiveManager.setLiveMode(false);
        }
    }

    private void setCameraBinning(int binning) throws Exception {
        logger.info("Set camera binning to " + binning);
        studio.getCMMCore().setProperty(Constants.cameraDevice, "Binning", binning);
    }

    private void setExposure(double exposureTimeInMs) throws Exception {
        logger.info("Set camera exposure to " + exposureTimeInMs/1000 + " s");
        studio.getCMMCore().setExposure(exposureTimeInMs);
    }
}
