package de.agbauer.physik.PeemState;

import de.agbauer.physik.Observers.AcquisitionParamsLoadListener;
import de.agbauer.physik.PeemCommunicator.PeemBulkReader;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class PEEMStateController extends Observable implements AcquisitionParamsLoadListener, DocumentListener {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PeemCommunicator peemCommunicator;
    private PEEMStateForm peemStateForm;

    public PEEMStateController(PeemCommunicator peemCommunicator, PEEMStateForm peemStateForm) {
        this.peemCommunicator = peemCommunicator;
        this.peemStateForm = peemStateForm;

        this.peemStateForm.readAllButton.addActionListener(this::readAllButtonClicked);
        this.peemStateForm.setAllButton.addActionListener(this::setAllButtonClicked);

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

        this.peemStateForm.extTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.focusTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.colTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.vxTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.vyTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.sxTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.syTextField.getDocument().addDocumentListener(this);
        this.peemStateForm.p1TextField.getDocument().addDocumentListener(this);
        this.peemStateForm.p2TextField.getDocument().addDocumentListener(this);
        this.peemStateForm.mcpTextfield.getDocument().addDocumentListener(this);
        this.peemStateForm.scrTextField.getDocument().addDocumentListener(this);
    }

    private void setAllButtonClicked(ActionEvent actionEvent) {

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

    private PeemVoltages peemVoltagesFromTextFields() {
        try {
            Map<PeemProperty, Double> peemVoltages = new HashMap<>();

            peemVoltages.put(PeemProperty.EXTRACTOR, Double.parseDouble(this.peemStateForm.extTextField.getText()));
            peemVoltages.put(PeemProperty.FOCUS, Double.parseDouble(this.peemStateForm.focusTextField.getText()));
            peemVoltages.put(PeemProperty.COLUMN, Double.parseDouble(this.peemStateForm.colTextField.getText()));
            peemVoltages.put(PeemProperty.DEFLECTOR_X, Double.parseDouble(this.peemStateForm.vxTextField.getText()));
            peemVoltages.put(PeemProperty.DEFLECTOR_Y, Double.parseDouble(this.peemStateForm.vyTextField.getText()));
            peemVoltages.put(PeemProperty.STIGMATOR_X, Double.parseDouble(this.peemStateForm.sxTextField.getText()));
            peemVoltages.put(PeemProperty.STIGMATOR_Y, Double.parseDouble(this.peemStateForm.syTextField.getText()));
            peemVoltages.put(PeemProperty.PROJECTIVE_1, Double.parseDouble(this.peemStateForm.p1TextField.getText()));
            peemVoltages.put(PeemProperty.PROJECTIVE_2, Double.parseDouble(this.peemStateForm.p2TextField.getText()));
            peemVoltages.put(PeemProperty.MCP, Double.parseDouble(this.peemStateForm.mcpTextfield.getText()));
            peemVoltages.put(PeemProperty.SCREEN, Double.parseDouble(this.peemStateForm.scrTextField.getText()));

            return new PeemVoltages(peemVoltages);
        } catch (NumberFormatException e) {
            return null;
        }

    }

    private void readAllButtonClicked(ActionEvent actionEvent) {
            CompletableFuture.runAsync(() -> {
                try {
                    PeemBulkReader bulkReader = new PeemBulkReader(peemCommunicator);
                    PeemVoltages peemVoltages = bulkReader.getAllVoltages();

                    this.notifyObservers();

                    updateUIWithVoltages(peemVoltages);

                    logger.info("Read all properties!");

                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }).exceptionally((e) -> {
                logger.severe("Couldn't read all params: " + e.getMessage());
                return null;
            });
    }

    private void updateUIWithVoltages(PeemVoltages peemVoltages) {
        String format = "%.1f";

        peemStateForm.extTextField.setText(String.format(format, peemVoltages.extractor));
        peemStateForm.focusTextField.setText(String.format(format, peemVoltages.focus));
        peemStateForm.colTextField.setText(String.format(format, peemVoltages.column));
        peemStateForm.p1TextField.setText(String.format(format, peemVoltages.projective1));
        peemStateForm.p2TextField.setText(String.format(format, peemVoltages.projective2));
        peemStateForm.vxTextField.setText(String.format(format, peemVoltages.stigmatorVx));
        peemStateForm.vyTextField.setText(String.format(format, peemVoltages.stigmatorVy));
        peemStateForm.sxTextField.setText(String.format(format, peemVoltages.stigmatorSx));
        peemStateForm.syTextField.setText(String.format(format, peemVoltages.stigmatorSy));
        peemStateForm.mcpTextfield.setText(String.format(format, peemVoltages.mcp));
        peemStateForm.scrTextField.setText(String.format(format, peemVoltages.screen));
    }

    @Override
    public void notifyObservers(Object arg) {
        this.setChanged();
        super.notifyObservers(peemVoltagesFromTextFields());
    }

    @Override
    public void peemVoltagesUpdated(Observable sender, PeemVoltages peemVoltages) {
        if (peemVoltages == null || sender == this) return;
        this.updateUIWithVoltages(peemVoltages);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.notifyObservers();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.notifyObservers();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }
}
