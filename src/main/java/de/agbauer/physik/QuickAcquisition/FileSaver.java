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
import java.util.Map;
import java.util.logging.Logger;

public class FileSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PEEMCommunicator peemCommunicator;

    public FileSaver(PEEMCommunicator peemCommunicator) {

        this.peemCommunicator = peemCommunicator;
    }

    public Map<PEEMProperty, String> savePEEMData(GeneralInformationData data) throws IOException {
        String exposureTimeInMs = JOptionPane.showInputDialog(
                null,
                "Enter the exposure time in ms",
                "Unknown exposure",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (exposureTimeInMs == null) {
            throw new IOException("User denied to enter an exposure time");
        }

        PEEMBulkReader bulkReader = new PEEMBulkReader(this.peemCommunicator);

        Map<PEEMProperty, String> allVoltages = bulkReader.getAllVoltages();
        Map<PEEMProperty, String> allCurrents = bulkReader.getAllCurrents();

        String workingDirectory = Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(data.sampleName);

        File workDir = new File(workingDirectory);
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new IOException("Couldn't create directory "+ workingDirectory);
        }

        AcquisitionParameters ap = new AcquisitionParameters(data, allVoltages, allCurrents);
        ap.exposure = exposureTimeInMs;

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String scopeName =  yearStr + "_" + data.sampleName;

        ap.imageNumber = getCurrentImageCountForDirectory(workingDirectory, scopeName);
        String imageNumberStr = String.format("%02d", ap.imageNumber);


        String filePath = workingDirectory + scopeName + "_" + imageNumberStr + "_" + data.excitation;

        logger.info("Saving peem params to " + filePath + "_PARAMS.txt");
        AcquisitionParametersFormatter formatter = new AcquisitionParametersFormatter(ap);
        String paramsString = formatter.format();
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath + "_PARAMS.txt"))) {
            out.print(paramsString);
        }

        logger.info("Successfully saved image and parameters!");

        return allVoltages;
    }

    void save(GeneralInformationData data, ImagePlus imagePlus, String exposure) throws IOException {
        logger.info("Saving image...");

        PEEMBulkReader bulkReader = new PEEMBulkReader(this.peemCommunicator);

        Map<PEEMProperty, String> allVoltages = bulkReader.getAllVoltages();
        Map<PEEMProperty, String> allCurrents = bulkReader.getAllCurrents();

        String workingDirectory = Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(data.sampleName);

        File workDir = new File(workingDirectory);
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new IOException("Couldn't create directory "+ workingDirectory);
        }

        AcquisitionParameters ap = new AcquisitionParameters(data, allVoltages, allCurrents);
        ap.exposure = exposure;

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String scopeName =  yearStr + "_" + data.sampleName;

        ap.imageNumber = getCurrentImageCountForDirectory(workingDirectory, scopeName);
        String imageNumberStr = String.format("%02d", ap.imageNumber);

        ij.io.FileSaver fileSaver = new ij.io.FileSaver(imagePlus);

        String filePath = workingDirectory + scopeName + "_" + imageNumberStr + "_" + data.excitation;

        logger.info("Saving image to " + filePath + ".tif");
        fileSaver.saveAsTiff(filePath + ".tif");

        logger.info("Saving peem params to " + filePath + "_PARAMS.txt");
        AcquisitionParametersFormatter formatter = new AcquisitionParametersFormatter(ap);
        String paramsString = formatter.format();
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath + "_PARAMS.txt"))) {
            out.print(paramsString);
        }

        logger.info("Successfully saved image and parameters!");
    }


    private int getCurrentImageCountForDirectory(String workingDirectory, String scopeName) {
        File folder = new File(workingDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return 1;
        }

        for (int i = listOfFiles.length - 1; i >= 0; i--) {
            File file = listOfFiles[i];

            if (file.getName().endsWith("_PARAMS.txt")) {
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
