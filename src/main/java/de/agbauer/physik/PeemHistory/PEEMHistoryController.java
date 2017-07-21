package de.agbauer.physik.PeemHistory;

import de.agbauer.physik.FileSystem.DataFiler;
import de.agbauer.physik.Observers.SampleNameChangeListener;
import de.agbauer.physik.PeemHistoryBrowser.PeemHistoryBrowserController;
import de.agbauer.physik.PeemHistoryBrowser.PeemHistoryBrowserForm;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersParser;
import de.agbauer.physik.Observers.DataSaveListeners;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersPowershellParser;
import ij.ImagePlus;

import javax.swing.*;
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
    private DataFiler dataFiler;
    private AcquisitionParametersParser acquisitionParametersParser = new AcquisitionParametersPowershellParser();
    private HistoryDataModel dataModel = new HistoryDataModel();
    private File workingDirectory;

    public PEEMHistoryController(PEEMHistoryForm form, DataFiler dataFiler) {

        this.form = form;
        this.dataFiler = dataFiler;
        this.form.historyTable.setModel(dataModel);
        this.form.historyTable.setAutoCreateRowSorter(dataModel.getRowCount() != 0);

        setColumnWidths();

        this.form.directoryLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount() == 2){
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

        this.form.historyTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JTable target = (JTable)e.getSource();
                    int rowIndex = target.getSelectedRow();
                    loadParamsFromRowWithIndex(rowIndex);
                    target.clearSelection();
                }
            }
        });
    }

    private void setColumnWidths() {
        Enumeration<TableColumn> columns = this.form.historyTable.getColumnModel().getColumns();
        while(columns.hasMoreElements()) {
            TableColumn column = columns.nextElement();
            column.setPreferredWidth(37);
        }
        this.form.historyTable.getColumnModel().getColumn(0).setPreferredWidth(12);
        this.form.historyTable.getColumnModel().getColumn(1).setPreferredWidth(45);
    }

    public void loadDirectory(File directory) {
        try {

            List<AcquisitionParameters> acquisitionParameters = new ArrayList<>();

            //The next line throws a NullPointerException, but the files are still loaded...?
            for (File file : directory.listFiles()) {

                if (dataFiler.isParamsTextFile(file)) {
                    acquisitionParameters.add(acquisitionParametersParser.parse(file));
                }
            }

            AcquisitionParameters[] arr = new AcquisitionParameters[acquisitionParameters.size()];

            dataModel.acquisitionParameters = acquisitionParameters.toArray(arr);
            this.form.historyTable.setAutoCreateRowSorter(dataModel.getRowCount() != 0);
        } catch (NullPointerException | IOException exc) {
            dataModel.acquisitionParameters = new AcquisitionParameters[]{};
        } finally {

            this.form.directoryLabel.setText("Dir: " + directory.getAbsolutePath());
            dataModel.fireTableDataChanged();
            form.historyTable.repaint();

        }
    }

    private void loadParamsFromRowWithIndex(int index){
        PeemVoltages peemVoltages = dataModel.acquisitionParameters[index].peemVoltages;

        this.setChanged();
        notifyObservers(peemVoltages);
    }

    @Override
    public void sampleNameChanged(String sampleName) {
        this.workingDirectory = new File(this.dataFiler.getWorkingDirectoryFor(sampleName));
        loadDirectory(this.workingDirectory);
    }

    @Override
    public void newDataSaved(ImagePlus image) {
        loadDirectory(this.workingDirectory);
    }
}
