package de.agbauer.physik.FileSystem;

import de.agbauer.physik.Constants;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DataFilerPeemLab implements DataFiler {

    public FileLocations setAcquisitionParams(AcquisitionParameters ap) {
        FileLocations fileLocations = new FileLocations();

        fileLocations.workingDirectory = getWorkingDirectoryFor(ap.generalData.sampleName);
        fileLocations.scopeName = generateScopeName(ap.generalData.sampleName);
        fileLocations.imageNumber = calculateImageNumber(ap.generalData.sampleName);
        ap.imageNumber = fileLocations.imageNumber;

        String absFileName = fileLocations.workingDirectory + fileLocations.scopeName + "_" + fileLocations.imageNumber + "_" + ap.generalData.excitation;

        fileLocations.tifImageFilePath = absFileName + ".tif";
        fileLocations.peemParametersFilePath = absFileName + "_PARAMS.txt";

        File directory = new File(fileLocations.workingDirectory);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        return fileLocations;
    }

    public String getWorkingDirectoryFor(String sampleName) {

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

    public int calculateImageNumber(String sampleName) {

        File folder = new File(getWorkingDirectoryFor(sampleName));
        File[] listOfFiles = folder.listFiles();

        int count = 1;

        if (listOfFiles == null) {
            return count;
        }

        for (File file : listOfFiles) {
            if (file.getName().endsWith("_PARAMS.txt")) {
                String remainingFilename = file.getName().substring(generateScopeName(sampleName).length() + 1);
                String imageCountStr = remainingFilename.substring(0, remainingFilename.indexOf("_"));

                try {
                    int newCount = Integer.parseInt(imageCountStr) + 1;
                    if (count < newCount) {
                        count = newCount;
                    }
                } catch (NumberFormatException ignored) {

                }
            }
        }
        return count;
    }

    public String generateScopeName(String sampleName) {
        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        return yearStr + "_" + sampleName;
    }

    @Override
    public String logLocation() {

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");

        Date now = new Date();

        String dateStr = date.format(now);
        return Constants.defaultLogFolder + dateStr + ".log";

    }

    public boolean isParamsTextFile(File file) {
        return !file.isDirectory() && file.getName().endsWith("_PARAMS.txt");
    }
}
