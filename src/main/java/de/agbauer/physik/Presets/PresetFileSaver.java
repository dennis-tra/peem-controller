package de.agbauer.physik.Presets;

import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class PresetFileSaver {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void save(PeemVoltages peemVoltages) throws IOException {
        logger.info("Saving current parameters as preset...");

        String yearStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        File defaultDir = new File(Constants.defaultPresetSaveFolder);

        JFileChooser jfc = new JFileChooser(defaultDir){
            @Override
            public void approveSelection(){
                File f = getSelectedFile();
                f = new File(ensurePstExtension(f.toString()));
                if(f.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                    }
                }
                super.approveSelection();
            }
        };

        jfc.setDialogTitle("Save preset");
        jfc.setSelectedFile(new File( yearStr + "_"));
        jfc.setFileFilter(new FileNameExtensionFilter("Preset file","pst"));

        int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File presetFile = jfc.getSelectedFile();

            String filePath = presetFile.toString();

            filePath = ensurePstExtension(filePath);


            FileOutputStream os = new FileOutputStream(filePath);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(peemVoltages);
            oos.close();

            logger.info("Saved peem params to " + filePath + ".pst");
        } else {
            logger.info("User cancelled saving preset");
        }

    }

    private String ensurePstExtension(String filePath) {
        if (!filePath.endsWith(".pst")) {
            filePath += ".pst";
        }
        return filePath;
    }

}
