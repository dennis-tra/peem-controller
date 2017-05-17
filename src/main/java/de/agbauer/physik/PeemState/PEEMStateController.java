package de.agbauer.physik.PeemState;

import de.agbauer.physik.Observers.AcquisitionParamsLoadListener;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.PeemCommunicator.PeemBulkReader;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersVoltages;
import de.agbauer.physik.QuickAcquisition.FileSaver;
import de.agbauer.physik.Presets.PresetFileSaver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.io.File;

public class PEEMStateController extends Observable implements SampleNameChangeListener, AcquisitionParamsLoadListener {
    private PeemCommunicator peemCommunicator;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private FileSaver fileSaver; // not necessary with new button function
    private PresetFileSaver presetSaver;
    private final JFileChooser fc = new JFileChooser();
    private PEEMStateForm peemStateForm;
    private String sampleName;

    public PEEMStateController(PeemCommunicator peemCommunicator, PEEMStateForm peemStateForm) {
        this.peemCommunicator = peemCommunicator;
        this.fileSaver = new FileSaver(peemCommunicator);
        this.presetSaver = new PresetFileSaver(peemCommunicator);
        this.peemStateForm = peemStateForm;

        this.peemStateForm.readAllButton.addActionListener(this::readAllButtonClicked);
        this.peemStateForm.readSaveButton.addActionListener(this::readSaveButtonClicked);
        this.peemStateForm.loadButton.addActionListener(this::loadButtonClicked);

        this.peemStateForm.setExtButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.EXTRACTOR, peemStateForm.extTextField));
        this.peemStateForm.setFocusButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.FOCUS, peemStateForm.focusTextField));
        this.peemStateForm.setColButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.COLUMN, peemStateForm.colTextField));
        this.peemStateForm.setVxButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.DEFLECTOR_X, peemStateForm.vxTextField));
        this.peemStateForm.setVyButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.DEFLECTOR_Y, peemStateForm.vyTextField));
        this.peemStateForm.setSxButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.STIGMATOR_X, peemStateForm.sxTextField));
        this.peemStateForm.setSyButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.STIGMATOR_Y, peemStateForm.syTextField));
        this.peemStateForm.setP1Button.addActionListener(e -> setPropertyFromTextField(PeemProperty.PROJECTIVE_1, peemStateForm.p1TextField));
        this.peemStateForm.setP2Button.addActionListener(e -> setPropertyFromTextField(PeemProperty.PROJECTIVE_2, peemStateForm.p2TextField));
        this.peemStateForm.setMcpButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.MCP, peemStateForm.mcpTextfield));
        this.peemStateForm.setScrButton.addActionListener(e -> setPropertyFromTextField(PeemProperty.SCREEN, peemStateForm.scrTextField));
    }

    private void setPropertyFromTextField(PeemProperty property, JTextField textField) {
        try {
            Float value = Float.parseFloat(textField.getText());
            peemCommunicator.setProperty(property, value);
        } catch (IOException e1) {
            logger.warning("Couldn't communicate with PEEM: " + e1.getMessage());
        } catch (NumberFormatException e2) {
            logger.warning("Invalid input for " + property.displayName() + ": '" + textField.getText() + "'");
        }
    }

    //old readSaveButtonClicked implementation to normally save params without PEEM image
    /*
    private void readSaveButtonClicked(ActionEvent actionEvent) {
        try {
            Map<PeemProperty, String> allVoltages = fileSaver.save(this.sampleName, null, null);
            updateUIWithVoltages(allVoltages);
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            logger.severe("Couldn't save PEEM params: " + e.getMessage());
        }
    }
    */

    private void readSaveButtonClicked(ActionEvent actionEvent) {
        try {
            Map<PeemProperty, String> allVoltages = presetSaver.save();
            updateUIWithVoltages(allVoltages);
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            logger.severe("Couldn't save PEEM params: " + e.getMessage());
        }
    }

    private void readAllButtonClicked(ActionEvent actionEvent) {

            CompletableFuture.runAsync(() -> {
                try {
                    PeemBulkReader bulkReader = new PeemBulkReader(peemCommunicator);
                    Map<PeemProperty, String> allVoltages = bulkReader.getAllVoltages();

                    updateUIWithVoltages(allVoltages);

                    logger.info("Read all properties!");

                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }).exceptionally((e) -> {
                logger.severe("Couldn't read all params: " + e.getMessage());
                return null;
            });

    }

    public void loadButtonClicked(ActionEvent actionEvent){

        int returnVal = fc.showOpenDialog(null);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File loadedPreset = fc.getSelectedFile();
            try{

                FileInputStream is = new FileInputStream(loadedPreset);
                ObjectInputStream ois = new ObjectInputStream(is);
                AcquisitionParametersVoltages loadedVoltages = (AcquisitionParametersVoltages) ois.readObject();
                ois.close();

                this.loadParams(loadedVoltages);
                logger.info("Loaded preset from file: " + loadedPreset);
            } catch (NullPointerException | ClassNotFoundException | IOException exc) {
                logger.info("Loading preset failed!");
            }
        }
    }

    private void updateUIWithVoltages(Map<PeemProperty, String> allVoltages) {
        peemStateForm.extTextField.setText(allVoltages.get(PeemProperty.EXTRACTOR));
        peemStateForm.focusTextField.setText(allVoltages.get(PeemProperty.FOCUS));
        peemStateForm.colTextField.setText(allVoltages.get(PeemProperty.COLUMN));
        peemStateForm.p1TextField.setText(allVoltages.get(PeemProperty.PROJECTIVE_1));
        peemStateForm.p2TextField.setText(allVoltages.get(PeemProperty.PROJECTIVE_2));
        peemStateForm.vxTextField.setText(allVoltages.get(PeemProperty.DEFLECTOR_X));
        peemStateForm.vyTextField.setText(allVoltages.get(PeemProperty.DEFLECTOR_Y));
        peemStateForm.sxTextField.setText(allVoltages.get(PeemProperty.STIGMATOR_X));
        peemStateForm.syTextField.setText(allVoltages.get(PeemProperty.STIGMATOR_Y));
        peemStateForm.mcpTextfield.setText(allVoltages.get(PeemProperty.MCP));
        peemStateForm.scrTextField.setText(allVoltages.get(PeemProperty.SCREEN));
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        this.peemStateForm.setEnabledState(!empty(this.sampleName));
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

    public void loadParams(AcquisitionParametersVoltages params) {
        peemStateForm.extTextField.setText(params.extractorU + "");
        peemStateForm.focusTextField.setText(params.focusU + "");
        peemStateForm.colTextField.setText(params.columnU + "");
        peemStateForm.p1TextField.setText(params.projective1U + "");
        peemStateForm.p2TextField.setText(params.projective2U + "");
        peemStateForm.vxTextField.setText(params.stigmatorVx + "");
        peemStateForm.vyTextField.setText(params.stigmatorVy+ "");
        peemStateForm.sxTextField.setText(params.stigmatorSx + "");
        peemStateForm.syTextField.setText(params.stigmatorSy + "");
        peemStateForm.mcpTextfield.setText(params.mcpU + "");
        peemStateForm.scrTextField.setText(params.screenU + "");
    }
}
