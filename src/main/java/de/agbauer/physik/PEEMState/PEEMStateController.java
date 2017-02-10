package de.agbauer.physik.PEEMState;

import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

/**
 * Created by dennis on 09/02/2017.
 */
public class PEEMStateController {
    private PEEMCommunicator peemCommunicator;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private PEEMStatePanel peemStatePanel;
    public PEEMStateController(PEEMCommunicator peemCommunicator, PEEMStatePanel peemStatePanel) {
        this.peemCommunicator = peemCommunicator;
        this.peemStatePanel = peemStatePanel;

        this.peemStatePanel.readAllButton.addActionListener(this::readAllButtonClicked);
    }

    private void readAllButtonClicked(ActionEvent actionEvent) {

            CompletableFuture.runAsync(() -> {
                try {
                    PEEMBulkReader bulkReader = new PEEMBulkReader(peemCommunicator);
                    Map<PEEMProperty, String> peemProperties = bulkReader.getAllVoltages();

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

                    logger.info("Read all properties!");

                } catch (IOException e) {
                    throw new CompletionException(e);
                }
            }).exceptionally((e) -> {
                logger.severe("Couldn't read all params: " + e.getMessage());
                return null;
            });

    }

}
