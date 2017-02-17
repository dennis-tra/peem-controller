package de.agbauer.physik.PEEMHistory;

import de.agbauer.physik.GeneralInformation.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.Generic.AcquisitionParameterParser;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.WorkingDirectory;
import de.agbauer.physik.Observers.DataSaveListeners;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;
import ij.ImagePlus;

import javax.swing.table.TableColumn;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PEEMHistoryController implements GeneralInformationChangeListener, DataSaveListeners {

    private final PEEMHistoryForm form;
    private HistoryDataModel dataModel = new HistoryDataModel();
    private GeneralInformationData generalInformationData;

    public PEEMHistoryController(PEEMHistoryForm form) {

        this.form = form;
        this.form.historyTable.setModel(dataModel);

        Enumeration<TableColumn> columns = this.form.historyTable.getColumnModel().getColumns();
        while(columns.hasMoreElements()){
            TableColumn column = (TableColumn) columns.nextElement();
            column.setPreferredWidth(37);
        }
        this.form.historyTable.getColumnModel().getColumn(0).setPreferredWidth(8);
        this.form.historyTable.getColumnModel().getColumn(1).setPreferredWidth(45);
    }

    private void loadDirectory(File directory) {
        try {

            List<AcquisitionParameters> acquisitionParameters = new ArrayList<>();
            for (File file : directory.listFiles()) {
                if (isParamsTextFile(file)) {
                    acquisitionParameters.add(AcquisitionParameterParser.parse(file));
                }
            }

            AcquisitionParameters[] arr = new AcquisitionParameters[acquisitionParameters.size()];
            dataModel.acquisitionParameters = acquisitionParameters.toArray(arr);

        } catch (NullPointerException | IOException exc) {

            dataModel.acquisitionParameters = new AcquisitionParameters[]{};

        } finally {

            this.form.directoryLabel.setText("Dir: " + directory.getAbsolutePath());
            dataModel.fireTableDataChanged();
            form.historyTable.repaint();

        }
    }

    private boolean isParamsTextFile(File file) {
        return !file.isDirectory() && file.getName().endsWith("_PARAMS.txt");
    }

    @Override
    public void generalInformationChanged(GeneralInformationData data) {
        this.generalInformationData = data;
        loadDirectory(new File(Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(this.generalInformationData.sampleName)));
    }

    @Override
    public void newDataSaved(ImagePlus image) {
        loadDirectory(new File(Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(this.generalInformationData.sampleName)));
    }
}
