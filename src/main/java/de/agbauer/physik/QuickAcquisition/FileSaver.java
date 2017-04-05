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

public class FileSaver {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PEEMCommunicator peemCommunicator;

    public FileSaver(PEEMCommunicator peemCommunicator) {

        this.peemCommunicator = peemCommunicator;
    }

    public void saveOptimisationSeries(String sampleName, List<ImagePlus> datastore) {

    }

    public Map<PEEMProperty, String>  save(String sampleName, ImagePlus imagePlus, String exposureTimeInMs) throws IOException {
        logger.info("Saving image...");

        GeneralInformationData data = askForExcitationApertureAndNote(sampleName);

        if (exposureTimeInMs == null) {
            exposureTimeInMs = askForExposureTimeInMs();
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

        if (imagePlus != null) {
            logger.info("Saving image to " + filePath + ".tif");

            ij.io.FileSaver fileSaver = new ij.io.FileSaver(imagePlus);
            fileSaver.saveAsTiff(filePath + ".tif");
        }

        logger.info("Saving peem params to " + filePath + "_PARAMS.txt");
        AcquisitionParametersFormatter formatter = new AcquisitionParametersFormatter(ap);
        String paramsString = formatter.format();
        try (PrintStream out = new PrintStream(new FileOutputStream(filePath + "_PARAMS.txt"))) {
            out.print(paramsString);
        }

        logger.info("Successfully saved!");

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

    private String askForExposureTimeInMs() throws IOException {
        String exposureTimeInMs = JOptionPane.showInputDialog(
                null,
                "Enter the exposure time in ms",
                "Unknown exposure",
                JOptionPane.INFORMATION_MESSAGE
        );

        if (exposureTimeInMs == null) {
            throw new IOException("User denied to enter an exposure time");
        }

        return exposureTimeInMs;
    }

    private GeneralInformationData askForExcitationApertureAndNote(String sampleName) throws IOException {

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

            GeneralInformationData data = new GeneralInformationData();
            data.sampleName = sampleName;
            data.excitation = saveParametersDialaog.excitationTextField.getText();
            data.aperture = (String) saveParametersDialaog.apertureComboBox.getSelectedItem();
            data.note = saveParametersDialaog.notesTextField.getText();

            return data;
        } else {
            throw new IOException("User denied to enter general information");
        }
    }
}
