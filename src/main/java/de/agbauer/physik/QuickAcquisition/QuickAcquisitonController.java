package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.LogManager;
import org.micromanager.SnapLiveManager;
import org.micromanager.Studio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dennis on 09/02/2017.
 */
public class QuickAcquisitonController {
    private final LogManager logManager;
    private final QuickAcquistionForm form;
    private Studio studio;

    public QuickAcquisitonController(Studio studio, LogManager logManager, QuickAcquistionForm form) {
        this.logManager = logManager;
        this.form = form;
        this.studio = studio;
        SnapLiveManager snapLiveManager = studio.getSnapLiveManager();

        this.form.liveButton.addActionListener(e -> {
            logManager.inform("Turning on Live mode", true, true);
            if (snapLiveManager.getIsLiveModeOn()) {
                snapLiveManager.setLiveMode(false);
            }

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
            logManager.inform("Snap an image", true, true);
            if (snapLiveManager.getIsLiveModeOn()) {
                snapLiveManager.setLiveMode(false);
            }

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

        });

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
