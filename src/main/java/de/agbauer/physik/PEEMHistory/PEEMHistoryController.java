package de.agbauer.physik.PEEMHistory;

import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.Generic.AcquisitionParameterParser;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.Generic.WorkingDirectory;
import de.agbauer.physik.Observers.DataSaveListeners;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;
import ij.ImagePlus;

import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Observable;
import java.util.logging.Logger;

public class PEEMHistoryController extends Observable implements SampleNameChangeListener, DataSaveListeners {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final PEEMHistoryForm form;
    private HistoryDataModel dataModel = new HistoryDataModel();
    private String sampleName;

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

        this.form.directoryLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2){
                    String pathName = form.directoryLabel.getText().substring("Dir: ".length());

                    if (pathName.isEmpty()) {
                        return;
                    }

                    try {
                        Desktop.getDesktop().open(new File(pathName));
                    } catch (IOException | IllegalArgumentException e1) {
                        logger.info("Couldn't open '" + pathName + "': " + e1.getMessage());
                    }
                }
            }
        });

        this.form.loadButton.addActionListener(e -> loadParamsFromTable());
    }

    private void loadDirectory(File directory) {
        try {

            List<AcquisitionParameters> acquisitionParameters = new ArrayList<>();

            //The next line throws a NullPointerException, but the files are still loaded...?
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

    //Looks up the selected line in the HistoryTable and sends the according params from the dataModel
    //to the Observer (AcquisitionParamsLoadObserver)
    private void loadParamsFromTable(){
        int selectedParameters = form.historyTable.getSelectedRow();
        if(dataModel.acquisitionParameters.length > selectedParameters && selectedParameters >= 0) {
            this.setChanged();
            notifyObservers(dataModel.acquisitionParameters[selectedParameters]);
        }
    }

    private boolean isParamsTextFile(File file) {
        return !file.isDirectory() && file.getName().endsWith("_PARAMS.txt");
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.sampleName = sampleName;
        loadDirectory(new File(Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(this.sampleName)));
    }

    @Override
    public void newDataSaved(ImagePlus image) {
        loadDirectory(new File(Constants.defaultFileSaveFolder + WorkingDirectory.getCurrentDirectory(this.sampleName)));
    }
}
