package de.agbauer.physik.Presets;

import de.agbauer.physik.Constants;
import de.agbauer.physik.Observers.AcquisitionParamsLoadListener;
import de.agbauer.physik.PeemCommunicator.PeemBulkSetter;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class PresetController extends Observable implements AcquisitionParamsLoadListener {
    private final PresetFileSaver presetSaver = new PresetFileSaver();;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PeemVoltages peemVoltages;
    private PeemCommunicator peemCommunicator;
    private PresetForm presetForm;

    public PresetController(PeemCommunicator peemCommunicator, PresetForm presetForm){
        this.peemCommunicator = peemCommunicator;
        this.presetForm = presetForm;

        this.presetForm.lowMagButton.addActionListener(e -> loadFavourite("lowMag"));
        this.presetForm.saveModeButton.addActionListener(e -> loadFavourite("saveMode"));
        this.presetForm.apertureButton.addActionListener(e -> loadFavourite("aperture"));
        this.presetForm.a1LensButton.addActionListener(e -> loadFavourite("1Lens"));
        this.presetForm.a2LensButton.addActionListener(e -> loadFavourite("2Lens"));
        this.presetForm.a3LensButton.addActionListener(e -> loadFavourite("3Lens"));

        this.presetForm.loadButton.addActionListener(this::loadButtonClicked);
        this.presetForm.saveButton.addActionListener(this::saveButtonClicked);
    }

    private void loadFavourite(String presetName){
        File presetFile = new File(Constants.defaultPresetSaveFolder + presetName + ".pst");
        this.loadPreset(presetFile);

        PeemBulkSetter bulkSetter = new PeemBulkSetter(peemCommunicator);
        CompletableFuture.runAsync(() -> {
            try {
                if (peemVoltages == null) {
                    throw new IOException("Could not parse text field values");
                }

                bulkSetter.setAllVoltages(peemVoltages);
                logger.info("Finished setting all peem voltages");
            } catch (IOException e) {
                logger.warning("Error while setting PEEM parameters: " + e.getMessage());
            }
        });
    }

    private void saveButtonClicked(ActionEvent actionEvent) {
        try {
            presetSaver.save(peemVoltages);
        } catch (IOException e) {
            logger.severe("Couldn't save PEEM params: " + e.getMessage());
        }
    }

    private void loadButtonClicked(ActionEvent actionEvent){

        JFileChooser fc = new JFileChooser(new File(Constants.defaultPresetSaveFolder));
        fc.setFileFilter(new FileNameExtensionFilter("Preset file","pst"));

        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {

            File presetFile = fc.getSelectedFile();
            this.loadPreset(presetFile);
        }
    }

    private void loadPreset(File presetFile) {

        try {
            FileInputStream is = new FileInputStream(presetFile);
            ObjectInputStream ois = new ObjectInputStream(is);

            PeemVoltages loadedVoltages = (PeemVoltages) ois.readObject();

            ois.close();

            this.setChanged();
            notifyObservers(loadedVoltages);

            logger.info("Loaded preset from file: " + presetFile);
        } catch(NullPointerException | ClassNotFoundException | IOException e){
            logger.severe("Preset could not be loaded: " + e.getMessage());
        }
    }

    @Override
    public void peemVoltagesUpdated(Observable sender, PeemVoltages peemVoltages) {
        if (sender == this) return;
        this.presetForm.saveButton.setEnabled(peemVoltages != null);
        this.peemVoltages = peemVoltages;
    }
}
