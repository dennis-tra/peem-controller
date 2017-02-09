package de.agbauer.physik.PEEMState;

import de.agbauer.physik.Generic.LogManager;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.PEEMCommunicator.PEEMQuantity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * Created by dennis on 09/02/2017.
 */
public class PEEMStateController {
    private PEEMCommunicator peemCommunicator;
    private LogManager logManager;
    private PEEMStatePanel peemStatePanel;
    public PEEMStateController(PEEMCommunicator peemCommunicator, LogManager logManager, PEEMStatePanel peemStatePanel) {
        this.peemCommunicator = peemCommunicator;
        this.logManager = logManager;
        this.peemStatePanel = peemStatePanel;

        this.peemStatePanel.readAllButton.addActionListener(this::readAllButtonClicked);
    }

    private void readAllButtonClicked(ActionEvent actionEvent) {

            CompletableFuture.runAsync(() -> {
                try {
                    Map<PEEMProperty, String> peemProperties = peemCommunicator.getAllProperties();

                    peemStatePanel.extTextField.setText(peemProperties.get(PEEMProperty.EXTRACTOR));
                    peemStatePanel.focusTextField.setText(peemProperties.get(PEEMProperty.FOCUS));
                    peemStatePanel.colTextField.setText(peemProperties.get(PEEMProperty.COLUMN));
                    peemStatePanel.p1TextField.setText(peemProperties.get(PEEMProperty.PROJECTIVE_1));
                    peemStatePanel.p2TextField.setText(peemProperties.get(PEEMProperty.PROJECTIVE_2));
                    peemStatePanel.vxTextField.setText(peemProperties.get(PEEMProperty.DEFLECTOR_X));
                    peemStatePanel.vyTextField.setText(peemProperties.get(PEEMProperty.DEFLECTOR_Y));
                    peemStatePanel.sxTextField.setText(peemProperties.get(PEEMProperty.STIGMATOR_X));
                    peemStatePanel.syTextField.setText(peemProperties.get(PEEMProperty.STIGMATOR_Y));
                    peemStatePanel.mcpTextfield.setText(peemProperties.get(PEEMProperty.MCP));
                    peemStatePanel.scrTextField.setText(peemProperties.get(PEEMProperty.SCREEN));

                    logManager.inform("Read all properties!", true, true);

                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }).exceptionally((e) -> {
                logManager.error("Couldn't read all params", (Exception) e, true);
                return null;
            });

    }

}
