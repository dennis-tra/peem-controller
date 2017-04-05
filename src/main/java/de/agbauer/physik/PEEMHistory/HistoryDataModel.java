package de.agbauer.physik.PEEMHistory;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DateFormatter;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class HistoryDataModel extends AbstractTableModel implements TableModel {

    private String[] columns = new String[]{
            "#", "time", "ext", "foc", "col", "Vx", "Vy", "Sx", "Sy", "p1", "p2", "mcp", "scr", "exc", "exp"
    };

    AcquisitionParameters[] acquisitionParameters = new AcquisitionParameters[]{};

    @Override
    public int getRowCount() {
        return acquisitionParameters.length;
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        AcquisitionParameters parameters = acquisitionParameters[rowIndex];
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");

        switch (columnIndex) {
            case 0: return parameters.imageNumber;
            case 1: return parameters.createdAt == null ? "": formatter.format(parameters.createdAt);
            case 2: return String.format(Locale.ROOT, "%.1f", parameters.extractorU).replaceAll(",", "");
            case 3: return String.format(Locale.ROOT, "%.1f", parameters.focusU).replaceAll(",", "");
            case 4: return String.format(Locale.ROOT, "%.1f", parameters.columnU).replaceAll(",", "");
            case 5: return String.format(Locale.ROOT, "%.3f", parameters.stigmatorVx).replaceAll(",", "");
            case 6: return String.format(Locale.ROOT, "%.3f", parameters.stigmatorVy).replaceAll(",", "");
            case 7: return String.format(Locale.ROOT, "%.3f", parameters.stigmatorSx).replaceAll(",", "");
            case 8: return String.format(Locale.ROOT, "%.3f", parameters.stigmatorSy).replaceAll(",", "");
            case 9: return String.format(Locale.ROOT, "%.1f", parameters.projective1U).replaceAll(",", "");
            case 10: return String.format(Locale.ROOT, "%.1f", parameters.projective2U).replaceAll(",", "");
            case 11: return String.format(Locale.ROOT, "%.1f", parameters.mcpU).replaceAll(",", "");
            case 12: return String.format(Locale.ROOT, "%.1f", parameters.screenU).replaceAll(",", "");
            case 13: return parameters.excitation == null ? "": parameters.excitation;
            case 14: return parameters.exposure == null ? "": parameters.exposure;
        }
        return "";
    }

}
