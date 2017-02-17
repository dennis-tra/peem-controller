package de.agbauer.physik.PEEMHistory;

import de.agbauer.physik.Generic.ActivatableForm;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dennis on 10/02/2017.
 */
public class PEEMHistoryForm implements ActivatableForm{
    private JPanel peemHistoryPanel;
    private JScrollPane historyScrollPane;
    JTable historyTable;
    JButton loadButton;
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

    @Override
    public void setEnabledState(boolean enabled) {
    }
}
