package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

public class PresetFileSaver {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PEEMCommunicator peemCommunicator;

    public PresetFileSaver(PEEMCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }

    public Map<PEEMProperty, String> save() throws IOException {
        logger.info("Saving current parameters as preset...");

        PEEMBulkReader bulkReader = new PEEMBulkReader(this.peemCommunicator);

        Map<PEEMProperty, String> allVoltages = bulkReader.getAllVoltages();

        // ask for preset name
        String presetName = (String)JOptionPane.showInputDialog(
                null,
                "Enter the preset name: ",
                "Saving as preset",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "untitled");

        String workingDirectory = Constants.defaultPresetSaveFolder;

        File workDir = new File(workingDirectory);
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new IOException("Couldn't create directory "+ workingDirectory);
        }

        //might have to manually put values for data
        AcquisitionParametersVoltages apVoltages = new AcquisitionParametersVoltages(allVoltages);

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String filePath = workingDirectory + yearStr + "_" + presetName;

        try {
            FileOutputStream os = new FileOutputStream(filePath + ".pst");
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(apVoltages);				// Here we write the actual serializable data into the file
            oos.close();

            logger.info("Saved peem params to " + filePath + ".pst");

        } catch (Exception e2) {
            logger.info("Saving failed!");
            e2.printStackTrace();
        }

        return allVoltages;
    }

}
