package de.agbauer.physik.Presets;

import de.agbauer.physik.Constants;
import de.agbauer.physik.PeemCommunicator.PeemBulkReader;
import de.agbauer.physik.PeemCommunicator.PeemCommunicator;
import de.agbauer.physik.PeemCommunicator.PeemProperty;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersVoltages;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

public class PresetFileSaver {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PeemCommunicator peemCommunicator;

    public PresetFileSaver(PeemCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }

    public Map<PeemProperty, String> save() throws IOException {
        logger.info("Saving current parameters as preset...");

        PeemBulkReader bulkReader = new PeemBulkReader(this.peemCommunicator);

        Map<PeemProperty, String> allVoltages = bulkReader.getAllVoltages();

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
