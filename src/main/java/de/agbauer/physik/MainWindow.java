package de.agbauer.physik;

import de.agbauer.physik.Generic.ActivatableForm;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesForm;
import de.agbauer.physik.PEEMHistory.PEEMHistoryForm;
import de.agbauer.physik.PEEMState.PEEMStatePanel;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionForm;

import javax.swing.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class MainWindow extends JFrame implements ActivatableForm {
    private JPanel rootPanel;
    JLabel statusBarLabel;
    public OptimisationSeriesForm optimisationSeriesForm;
    JTextField probeNameTextField;
    PEEMStatePanel peemStatePanel;
    QuickAcquisitionForm quickAcquisitionForm;
    JComboBox apertureComboBox;
    JTextField excitationTextField;
    PEEMHistoryForm peemHistoryForm;

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

    @Override
    public void setEnabledState(boolean enabled) {
        apertureComboBox.setEnabled(enabled);
        excitationTextField.setEnabled(enabled);
        probeNameTextField.setEnabled(enabled);
    }
}
