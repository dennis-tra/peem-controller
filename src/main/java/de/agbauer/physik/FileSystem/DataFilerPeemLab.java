package de.agbauer.physik.FileSystem;

import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFilerPeemLab implements DataFiler {
    private String workingDirectory;
    private int imageNumber;
    private String scopeName;
    private String tifImageFilePath;
    private String peemParametersFilePath;
    private AcquisitionParameters ap;

    public void setAcquisitionParams(AcquisitionParameters ap) {
        this.ap = ap;
        this.workingDirectory = generateWorkingDirectory(ap.generalData.sampleName);
        this.scopeName = generateScopeName(ap.generalData.sampleName);
        this.imageNumber = calculateImageNumber(ap.generalData.sampleName);
        ap.imageNumber = this.imageNumber;

        String absFileName = this.workingDirectory + this.scopeName + "_" + this.imageNumber + "_" + ap.generalData.excitation;

        this.tifImageFilePath = absFileName + ".tif";
        this.peemParametersFilePath = absFileName + "_PARAMS.txt";
    }

    private String generateWorkingDirectory(String sampleName) {

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthName = new SimpleDateFormat("MMMM", Locale.GERMAN);
        SimpleDateFormat monthNumber = new SimpleDateFormat("MM");

        Date now = new Date();

        String dateStr = date.format(now);
        String yearStr = year.format(now);
        String monthNameStr = monthName.format(now);
        String monthNumberStr = monthNumber.format(now);

        return Constants.defaultFileSaveFolder + yearStr + File.separator +
                monthNumberStr + "_" + monthNameStr + File.separator +
                dateStr + "_" + sampleName + File.separator;

    }

    private int calculateImageNumber(String sampleName) {

        File folder = new File(generateWorkingDirectory(sampleName));
        File[] listOfFiles = folder.listFiles();

        if (listOfFiles == null) {
            return 1;
        }

        for (int i = listOfFiles.length - 1; i >= 0; i--) {
            File file = listOfFiles[i];

            if (file.getName().endsWith("_PARAMS.txt")) {
                String remainingFilename = file.getName().substring(generateScopeName(sampleName).length() + 1);
                String imageCountStr = remainingFilename.substring(0, remainingFilename.indexOf("_"));

                try {
                    return Integer.parseInt(imageCountStr) + 1;
                } catch (NumberFormatException exc) {

                }
            }
        }
        return 1;
    }

    private String generateScopeName(String sampleName) {
        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return yearStr + "_" + sampleName;
    }

    @Override
    public String getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public String getScopeName() {
        return this.scopeName;
    }

    @Override
    public String getTifImageFilePath() {
        return this.tifImageFilePath;
    }

    @Override
    public String getPeemParametersFilePath() {
        return this.peemParametersFilePath;
    }

    @Override
    public int getImageNumber() {
        return this.imageNumber;
    }

}
