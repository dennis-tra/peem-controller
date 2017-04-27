package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.WorkingDirectory;
import de.agbauer.physik.PEEMCommunicator.PEEMBulkReader;
import de.agbauer.physik.PEEMCommunicator.PEEMCommunicator;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;
import ij.ImagePlus;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        Map<PEEMProperty, String> allCurrents = bulkReader.getAllCurrents();

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

        GeneralInformationData data = new GeneralInformationData();
        //might have to manually put values for data
        AcquisitionParameters ap = new AcquisitionParameters(data, allVoltages, allCurrents);
        ap.exposure = null;

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        ap.imageNumber = getCurrentImageCountForDirectory(workingDirectory, yearStr);
        String imageNumberStr = String.format("%02d", ap.imageNumber);


        String filePath = workingDirectory + yearStr + "_" + imageNumberStr + "_" + presetName;

        if(presetName != null) {
            logger.info("Saving peem params to " + filePath + "_PRESET.txt");
            AcquisitionParametersFormatter formatter = new AcquisitionParametersFormatter(ap);
            String paramsString = formatter.format();
            try (PrintStream out = new PrintStream(new FileOutputStream(filePath + "_PRESET.txt"))) {
                out.print(paramsString);
            }

            logger.info("Successfully saved!");
        }

        return allVoltages;
    }


    private int getCurrentImageCountForDirectory(String workingDirectory, String scopeName) {
        File folder = new File(workingDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return 1;
        }

        for (int i = listOfFiles.length - 1; i >= 0; i--) {
            File file = listOfFiles[i];

            if (file.getName().endsWith("_PRESET.txt")) {
                String remainingFilename = file.getName().substring(scopeName.length() + 1);
                String imageCountStr = remainingFilename.substring(0, remainingFilename.indexOf("_"));

                try {
                    return Integer.parseInt(imageCountStr) + 1;
                } catch (NumberFormatException exc) {

                }
            }
        }
        return 1;
    }

}
