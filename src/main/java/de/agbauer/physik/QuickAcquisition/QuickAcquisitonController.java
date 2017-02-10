package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.LogManager;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.PersistenceHandler;
import ij.ImagePlus;
import org.micromanager.SnapLiveManager;
import org.micromanager.Studio;

import javax.swing.*;
import java.util.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class QuickAcquisitonController {
    private final LogManager logManager;
    private final QuickAcquistionForm form;
    private Studio studio;
    private PEEMCommunicator peemCommunicator;
    private PersistenceHandler persistenceHandler;

    private SnapLiveManager snapLiveManager;

    public QuickAcquisitonController(Studio studio, PersistenceHandler generalInformation, PEEMCommunicator peemCommunicator, LogManager logManager, QuickAcquistionForm form) {
        this.logManager = logManager;
        this.form = form;
        this.studio = studio;
        this.peemCommunicator = peemCommunicator;
        this.persistenceHandler = generalInformation;
        this.snapLiveManager = studio.getSnapLiveManager();

        this.form.liveButton.addActionListener(e -> {
            logManager.inform("Turning on Live mode", true, true);
            turnOffLiveMode();

            String exposureStr = this.form.liveTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.liveComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);
                snapLiveManager.setLiveMode(true);

            } catch (Exception e1) {
                logManager.error("Couldn't turn live mode on", e1, true);
            }
        });

        this.form.snapButton.addActionListener(e -> {
            logManager.inform("Acquiring image...", true, true);
            turnOffLiveMode();

            String exposureStr = this.form.snapTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.snapComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);
                snapLiveManager.snap(true);

            } catch (Exception e1) {
                logManager.error("Couldn't snap an image", e1, true);
            }
        });

        this.form.snapPlusButton.addActionListener(e -> {

            if (!persistenceHandler.isAllInformationAvailable()) {
                logManager.inform("Error: Please enter a name and excitation necessary information", true ,false);
                return;
            }

            logManager.inform("Acquiring image...", true, true);
            turnOffLiveMode();

            String exposureStr = this.form.snapPlusTextField.getText();

            float exposureInMs = Float.parseFloat(exposureStr);
            int binning = Integer.parseInt((String) this.form.snapPlusComboBox.getSelectedItem());

            try {
                setExposure(exposureInMs);
                setCameraBinning(binning);

                PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator, logManager);

                Map<PEEMProperty, String> peemVoltages = bulkReader.getAllVoltages();
                Map<PEEMProperty, String> peemCurrents = bulkReader.getAllCurrents();

                snapLiveManager.snap(true).get(0);
                ImagePlus imagePlus = snapLiveManager.getDisplay().getImagePlus();

                int dialogResult = JOptionPane.showConfirmDialog(null, "Do you want to save this image?", "Save image dialog", JOptionPane.YES_NO_OPTION);

                if(dialogResult == JOptionPane.YES_OPTION) {
                    logManager.inform("Saving image...", true, true);
                    persistenceHandler.saveImage(imagePlus);
                } else {
                    logManager.inform("User denied saving image", false, true);
                }

            } catch (Exception e1) {
                logManager.error("Couldn't snap an image: ", e1, true);
            }
        });

        this.form.stopButton.addActionListener(e -> turnOffLiveMode());

    }

    private void turnOffLiveMode() {
        logManager.inform("Stopping live acquisition", true, true);

        if (snapLiveManager.getIsLiveModeOn()) {
            snapLiveManager.setLiveMode(false);
        }
    }

    private void setCameraBinning(int binning) throws Exception {
        logManager.inform("Set camera binning to " + binning, true, true);
        studio.getCMMCore().setProperty(Constants.cameraDevice, "Binning", binning);
    }

    private void setExposure(double exposureTimeInMs) throws Exception {
        logManager.inform("Set camera exposure to " + exposureTimeInMs/1000 + " s", true, true);
        studio.getCMMCore().setExposure(exposureTimeInMs);
    }
}
