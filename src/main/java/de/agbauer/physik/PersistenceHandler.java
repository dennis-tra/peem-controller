package de.agbauer.physik;

import de.agbauer.physik.Generic.Constants;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by dennis on 10/02/2017.
 */
public class PersistenceHandler {
    private GeneralInformation generalInformation;

    PersistenceHandler(GeneralInformation generalInformation) {
        this.generalInformation = generalInformation;
    }

    public boolean isAllInformationAvailable() {
        return !generalInformation.getProbeName().isEmpty() && !generalInformation.getExcitation().isEmpty();
    }

    private String getScopeName() {
        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String probeName = generalInformation.getProbeName();

        return yearStr + "_" + probeName;
    }

    private String getWorkingDirectory() {

        return Constants.defaultFileSaveFolder + getScopeName() + File.separator;
    }

    public void saveImage(ImagePlus imagePlus) {

        String savePath = getSavePathForSingleImage();

        new File(getWorkingDirectory()).mkdir();

        FileSaver saver = new FileSaver(imagePlus);
        saver.saveAsTiff(savePath);
    }

    public void savePeemParams() {

    }

    private String getSavePathForSingleImage() {

        String workingDirectory = getWorkingDirectory();
        int count = getCurrentImageCountForDirectory(workingDirectory);
        String scopeName = getScopeName();

        String excitation = generalInformation.getExcitation();
        return workingDirectory + scopeName + "_" + String.format("%02d", count) + "_" + excitation + ".tif";
    }

    private int getCurrentImageCountForDirectory(String workingDirectory) {
        File folder = new File(workingDirectory);
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return 1;
        }


        for (int i = listOfFiles.length - 1; i >= 0; i--) {
            File file = listOfFiles[i];
            String scopeName = getScopeName();

            if (file.isFile() && getFileExtension(file).equals("tif") && file.getName().startsWith(scopeName + "_")) {
                String filename = file.getName();
                String remainingFilename = filename.substring(scopeName.length() + 1);
                String imageCountStr = remainingFilename.substring(0, remainingFilename.indexOf("_"));

                try {
                    return Integer.parseInt(imageCountStr) + 1;
                } catch (NumberFormatException exc) {

                }
            }
        }
        return 1;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        try {
            return name.substring(name.lastIndexOf(".") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
