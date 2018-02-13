package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.*;
import de.agbauer.physik.PeemCommunicator.PeemBulkReader;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;

import javax.swing.*;
import java.io.IOException;
import java.util.Map;

public class AcquisitionParametersCollector {
    private final PeemCommunicator peemCommunicator;

    public AcquisitionParametersCollector(PeemCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }

    public AcquisitionParameters collectSilently(CameraData cameraData, GeneralAcquisitionData generalData) throws IOException {

        PeemBulkReader bulkReader = new PeemBulkReader(this.peemCommunicator);

        PeemVoltages peemVoltages = bulkReader.getAllVoltages();
        PeemCurrents peemCurrents = bulkReader.getAllCurrents();

        AcquisitionParameters ap = new AcquisitionParameters(generalData, peemVoltages, peemCurrents, cameraData);

        return ap;
    }

    public AcquisitionParameters collect(String sampleName, CameraData cameraData) throws IOException {

        GeneralAcquisitionData generalData = askForExcitationApertureAndNote(sampleName);

        PeemBulkReader bulkReader = new PeemBulkReader(this.peemCommunicator);

        PeemVoltages peemVoltages = bulkReader.getAllVoltages();
        PeemCurrents peemCurrents = bulkReader.getAllCurrents();

        AcquisitionParameters ap = new AcquisitionParameters(generalData, peemVoltages, peemCurrents, cameraData);

        return ap;
    }

    private GeneralAcquisitionData askForExcitationApertureAndNote(String sampleName) throws IOException {

        SaveParameterDialog saveParametersDialaog = new SaveParameterDialog();

        Object[] dialogOptions = { "Save", "Cancel" };

        int result = JOptionPane.showOptionDialog(null,
                saveParametersDialaog.contentPane,
                "Enter external parameters",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                dialogOptions,
                dialogOptions[0]);

        if (result == JOptionPane.YES_OPTION){

            saveParametersDialaog.saveSelectedParams();

            String excitation = saveParametersDialaog.excitationTextField.getText();
            String aperture = (String) saveParametersDialaog.apertureComboBox.getSelectedItem();
            String note = saveParametersDialaog.notesTextField.getText();

            return new GeneralAcquisitionData(sampleName, excitation, aperture, note);
        } else {
            throw new IOException("User denied to enter general information");
        }
    }
}
