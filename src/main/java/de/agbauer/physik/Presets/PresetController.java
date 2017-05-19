package de.agbauer.physik.Presets;


import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Observable;
import java.util.logging.Logger;

public class PresetController extends Observable{
    private PresetForm presetForm;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public PresetController(PresetForm presetWindow){
        this.presetForm = presetWindow;
        this.presetForm.lowMagButton.addActionListener(e -> loadPreset("lowMag"));
        this.presetForm.a1LensButton.addActionListener(e -> loadPreset("1Lens"));
        this.presetForm.a2LensButton.addActionListener(e -> loadPreset("2Lens"));
        this.presetForm.a3LensButton.addActionListener(e -> loadPreset("3Lens"));
        this.presetForm.apertureButton.addActionListener(e -> loadPreset("aperture"));

    }

    private void loadPreset(String presetName){
        try {
            File loadedPreset = new File(Constants.defaultPresetSaveFolder + presetName + ".pst");
            FileInputStream is = new FileInputStream(loadedPreset);
            ObjectInputStream ois = new ObjectInputStream(is);
            PeemVoltages loadedVoltages = (PeemVoltages) ois.readObject();
            ois.close();

            this.setChanged();
            notifyObservers(loadedVoltages);
            logger.info("Loaded preset from file: " + loadedPreset);
        }catch(NullPointerException | ClassNotFoundException | IOException e){
            logger.severe("Preset could not be loaded!");
        }
    }
}
