package de.agbauer.physik.Presets;

import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class PresetFileSaver {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void save(PeemVoltages peemVoltages) throws IOException {
        logger.info("Saving current parameters as preset...");

        // ask for preset name
        String presetName = (String)JOptionPane.showInputDialog(
                null,
                "Enter the preset name: ",
                "Save preset",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "untitled");

        String workingDirectory = Constants.defaultPresetSaveFolder;

        File workDir = new File(workingDirectory);
        if (!workDir.exists() && !workDir.mkdirs()) {
            throw new IOException("Couldn't create directory "+ workingDirectory);
        }

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());

        String filePath = workingDirectory + yearStr + "_" + presetName;

        FileOutputStream os = new FileOutputStream(filePath + ".pst");
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeObject(peemVoltages);				// Here we write the actual serializable data into the file
        oos.close();

        logger.info("Saved peem params to " + filePath + ".pst");
    }

}
