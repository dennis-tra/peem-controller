package de.agbauer.physik;

import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesForm;
import de.agbauer.physik.PEEMHistory.PEEMHistoryForm;
import de.agbauer.physik.PEEMState.PEEMStatePanel;
import de.agbauer.physik.QuickAcquisition.QuickAcquistionForm;

import javax.swing.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class MainWindow extends JFrame {
    private JPanel rootPanel;
    JLabel statusBarLabel;
    OptimisationSeriesForm optimisationSeriesForm;
    JTextField probeNameTextField;
    PEEMStatePanel peemStatePanel;
    QuickAcquistionForm quickAcquistionForm;
    JComboBox apertureComboBox;
    private PEEMHistoryForm PEEMHistoryForm1;
    JTextField excitationTextField;
    private JTextArea textArea1;
    private JTabbedPane tabbedPane1;

    MainWindow() {
        super();

        setContentPane(rootPanel);
        pack();

        apertureComboBox.addItem("50");
        apertureComboBox.addItem("75");
        apertureComboBox.addItem("500");
        apertureComboBox.addItem("1500");
        apertureComboBox.setSelectedItem("500");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            System.out.print("Couldn't load system look and feel for ");
        }

        setTitle("PEEM Controller " + Constants.version);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
