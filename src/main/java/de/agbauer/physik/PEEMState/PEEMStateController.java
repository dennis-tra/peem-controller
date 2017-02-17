package de.agbauer.physik.PEEMState;

import de.agbauer.physik.GeneralInformation.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import de.agbauer.physik.QuickAcquisition.FileSaver;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.logging.Logger;

public class PEEMStateController extends Observable implements GeneralInformationChangeListener {
    private PEEMCommunicator peemCommunicator;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private FileSaver fileSaver;
    private PEEMStatePanel peemStatePanel;
    private GeneralInformationData generalInformationData;

    public PEEMStateController(PEEMCommunicator peemCommunicator, PEEMStatePanel peemStatePanel) {
        this.peemCommunicator = peemCommunicator;
        this.fileSaver = new FileSaver(peemCommunicator);
        this.peemStatePanel = peemStatePanel;

        this.peemStatePanel.readAllButton.addActionListener(this::readAllButtonClicked);
        this.peemStatePanel.readSaveButton.addActionListener(this::readSaveButtonClicked);
    }

    private void readSaveButtonClicked(ActionEvent actionEvent) {
        try {
            Map<PEEMProperty, String> allVoltages = fileSaver.savePEEMData(this.generalInformationData);
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

    private void updateUIWithVoltages(Map<PEEMProperty, String> allVoltages) {
        peemStatePanel.extTextField.setText(allVoltages.get(PEEMProperty.EXTRACTOR));
        peemStatePanel.focusTextField.setText(allVoltages.get(PEEMProperty.FOCUS));
        peemStatePanel.colTextField.setText(allVoltages.get(PEEMProperty.COLUMN));
        peemStatePanel.p1TextField.setText(allVoltages.get(PEEMProperty.PROJECTIVE_1));
        peemStatePanel.p2TextField.setText(allVoltages.get(PEEMProperty.PROJECTIVE_2));
        peemStatePanel.vxTextField.setText(allVoltages.get(PEEMProperty.DEFLECTOR_X));
        peemStatePanel.vyTextField.setText(allVoltages.get(PEEMProperty.DEFLECTOR_Y));
        peemStatePanel.sxTextField.setText(allVoltages.get(PEEMProperty.STIGMATOR_X));
        peemStatePanel.syTextField.setText(allVoltages.get(PEEMProperty.STIGMATOR_Y));
        peemStatePanel.mcpTextfield.setText(allVoltages.get(PEEMProperty.MCP));
        peemStatePanel.scrTextField.setText(allVoltages.get(PEEMProperty.SCREEN));
    }

    @Override
    public void generalInformationChanged(GeneralInformationData data) {
        this.generalInformationData = data;
    }
}
