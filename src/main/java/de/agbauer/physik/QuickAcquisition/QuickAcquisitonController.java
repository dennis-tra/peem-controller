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
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by dennis on 09/02/2017.
 */
public class QuickAcquisitonController {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final QuickAcquistionForm form;
    private Studio studio;
    private PEEMCommunicator peemCommunicator;
    private PersistenceHandler persistenceHandler;

    private SnapLiveManager snapLiveManager;

    public QuickAcquisitonController(Studio studio, PersistenceHandler generalInformation, PEEMCommunicator peemCommunicator, QuickAcquistionForm form) {
        this.form = form;
        this.studio = studio;
        this.peemCommunicator = peemCommunicator;
        this.persistenceHandler = generalInformation;
        this.snapLiveManager = studio.getSnapLiveManager();

        this.form.liveButton.addActionListener(e -> {
            logger.info("Turning on Live mode");
            turnOffLiveMode();

            String exposureStr = this.form.liveTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.liveComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);
                snapLiveManager.setLiveMode(true);

            } catch (Exception e1) {
                logger.severe("Couldn't turn live mode on : " + e1.getMessage());
            }
        });

        this.form.snapButton.addActionListener(e -> {
            logger.info("Acquiring image...");
            turnOffLiveMode();

            String exposureStr = this.form.snapTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.snapComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);
                snapLiveManager.snap(true);

            } catch (Exception e1) {
                logger.severe("Couldn't snap an image : " + e1.getMessage());
            }
        });

        this.form.snapPlusButton.addActionListener(e -> {

            if (!persistenceHandler.isAllInformationAvailable()) {
                logger.info("Error: Please enter a name and excitation necessary information");
                return;
            }

            logger.info("Acquiring image...");
            turnOffLiveMode();

            String exposureStr = this.form.snapPlusTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.snapPlusComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);

                PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);

                Map<PEEMProperty, String> peemVoltages = bulkReader.getAllVoltages();
                Map<PEEMProperty, String> peemCurrents = bulkReader.getAllCurrents();

                snapLiveManager.snap(true).get(0);
                ImagePlus imagePlus = snapLiveManager.getDisplay().getImagePlus();

                int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save this image?", "Save image dialog", JOptionPane.YES_NO_OPTION);

                if(dialogResult == JOptionPane.YES_OPTION) {
                    logger.info("Saving image...");
                    persistenceHandler.saveImage(imagePlus);
                } else {
                    logger.info("User denied saving image");
                }

            } catch (Exception e1) {
                logger.severe("Couldn't snap an image: " + e1.getMessage());
            }
        });

        this.form.stopButton.addActionListener(e -> turnOffLiveMode());

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
