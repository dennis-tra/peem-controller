package de.agbauer.physik.PeemHistory;

import javax.swing.*;
import java.awt.*;

public class PEEMHistoryForm {
    private JPanel peemHistoryPanel;
    private JScrollPane historyScrollPane;
    JTable historyTable;
    JLabel directoryLabel;

    public PEEMHistoryForm() {
        Font font = historyTable.getFont();
        historyTable.setFont(new Font(font.getName(), font.getStyle(), 9));
        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        historyScrollPane.createHorizontalScrollBar();

        Font font2 = directoryLabel.getFont();
        directoryLabel.setFont(new Font(font2.getName(), font2.getStyle(), 8));
    }
}
