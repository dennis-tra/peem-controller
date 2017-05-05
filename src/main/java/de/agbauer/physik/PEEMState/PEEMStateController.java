package de.agbauer.physik.PEEMState;

import de.agbauer.physik.Generic.AcquisitionParameterParser;
import de.agbauer.physik.Observers.AcquisitionParamsLoadListener;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.FileSaver;
import de.agbauer.physik.QuickAcquisition.PresetFileSaver;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;
import java.io.File;

public class PEEMStateController extends Observable implements SampleNameChangeListener, AcquisitionParamsLoadListener {
    private PEEMCommunicator peemCommunicator;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private FileSaver fileSaver; // not necessary with new button function
    private PresetFileSaver presetSaver;
    private final JFileChooser fc = new JFileChooser();
    private PEEMStateForm peemStateForm;
    private String sampleName;

    public PEEMStateController(PEEMCommunicator peemCommunicator, PEEMStateForm peemStateForm) {
        this.peemCommunicator = peemCommunicator;
        this.fileSaver = new FileSaver(peemCommunicator);
        this.presetSaver = new PresetFileSaver(peemCommunicator);
        this.peemStateForm = peemStateForm;

        this.peemStateForm.readAllButton.addActionListener(this::readAllButtonClicked);
        this.peemStateForm.readSaveButton.addActionListener(this::readSaveButtonClicked);
        this.peemStateForm.loadButton.addActionListener(this::loadButtonClicked);

        this.peemStateForm.setExtButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.EXTRACTOR, peemStateForm.extTextField));
        this.peemStateForm.setFocusButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.FOCUS, peemStateForm.focusTextField));
        this.peemStateForm.setColButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.COLUMN, peemStateForm.colTextField));
        this.peemStateForm.setVxButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.DEFLECTOR_X, peemStateForm.vxTextField));
        this.peemStateForm.setVyButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.DEFLECTOR_Y, peemStateForm.vyTextField));
        this.peemStateForm.setSxButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.STIGMATOR_X, peemStateForm.sxTextField));
        this.peemStateForm.setSyButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.STIGMATOR_Y, peemStateForm.syTextField));
        this.peemStateForm.setP1Button.addActionListener(e -> setPropertyFromTextField(PEEMProperty.PROJECTIVE_1, peemStateForm.p1TextField));
        this.peemStateForm.setP2Button.addActionListener(e -> setPropertyFromTextField(PEEMProperty.PROJECTIVE_2, peemStateForm.p2TextField));
        this.peemStateForm.setMcpButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.MCP, peemStateForm.mcpTextfield));
        this.peemStateForm.setScrButton.addActionListener(e -> setPropertyFromTextField(PEEMProperty.SCREEN, peemStateForm.scrTextField));
    }

    private void setPropertyFromTextField(PEEMProperty property, JTextField textField) {
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
            Map<PEEMProperty, String> allVoltages = fileSaver.save(this.sampleName, null, null);
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
            Map<PEEMProperty, String> allVoltages = presetSaver.save();
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
                    PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);
                    Map<PEEMProperty, String> allVoltages = bulkReader.getAllVoltages();

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
                this.loadParams(AcquisitionParameterParser.parse(loadedPreset));
            } catch (NullPointerException | IOException exc) {
                logger.info("Loading preset failed!");
            }
            logger.info("Loaded preset from file: " + loadedPreset);
        }
    }

    private void updateUIWithVoltages(Map<PEEMProperty, String> allVoltages) {
        peemStateForm.extTextField.setText(allVoltages.get(PEEMProperty.EXTRACTOR));
        peemStateForm.focusTextField.setText(allVoltages.get(PEEMProperty.FOCUS));
        peemStateForm.colTextField.setText(allVoltages.get(PEEMProperty.COLUMN));
        peemStateForm.p1TextField.setText(allVoltages.get(PEEMProperty.PROJECTIVE_1));
        peemStateForm.p2TextField.setText(allVoltages.get(PEEMProperty.PROJECTIVE_2));
        peemStateForm.vxTextField.setText(allVoltages.get(PEEMProperty.DEFLECTOR_X));
        peemStateForm.vyTextField.setText(allVoltages.get(PEEMProperty.DEFLECTOR_Y));
        peemStateForm.sxTextField.setText(allVoltages.get(PEEMProperty.STIGMATOR_X));
        peemStateForm.syTextField.setText(allVoltages.get(PEEMProperty.STIGMATOR_Y));
        peemStateForm.mcpTextfield.setText(allVoltages.get(PEEMProperty.MCP));
        peemStateForm.scrTextField.setText(allVoltages.get(PEEMProperty.SCREEN));
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        this.peemStateForm.setEnabledState(!empty(this.sampleName));
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

    @Override
    public void loadParams(AcquisitionParameters params) {
        peemStateForm.extTextField.setText(params.getExtractorU() + "");
        peemStateForm.focusTextField.setText(params.getFocusU() + "");
        peemStateForm.colTextField.setText(params.getColumnU() + "");
        peemStateForm.p1TextField.setText(params.getProjective1U() + "");
        peemStateForm.p2TextField.setText(params.getProjective2U() + "");
        peemStateForm.vxTextField.setText(params.getStigmatorVx() + ""); // stigmatorVx richtig?
        peemStateForm.vyTextField.setText(params.getStigmatorVy()+ "");
        peemStateForm.sxTextField.setText(params.getStigmatorSx() + "");
        peemStateForm.syTextField.setText(params.getStigmatorSy() + "");
        peemStateForm.mcpTextfield.setText(params.getMcpU() + "");
        peemStateForm.scrTextField.setText(params.getScreenU() + "");
    }
}
