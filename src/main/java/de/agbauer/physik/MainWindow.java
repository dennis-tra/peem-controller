package de.agbauer.physik;

import de.agbauer.physik.GeneralInformation.GeneralInformationForm;
import de.agbauer.physik.Generic.Constants;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesForm;
import de.agbauer.physik.PEEMHistory.PEEMHistoryForm;
import de.agbauer.physik.PEEMState.PEEMStatePanel;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionForm;

import javax.swing.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class MainWindow extends JFrame {
    private JPanel rootPanel;
    JLabel statusBarLabel;
    public OptimisationSeriesForm optimisationSeriesForm;
    PEEMStatePanel peemStatePanel;
    QuickAcquisitionForm quickAcquisitionForm;
    GeneralInformationForm generalInformationForm;
    PEEMHistoryForm peemHistoryForm;

    MainWindow() {
        super();

        setContentPane(rootPanel);
        pack();

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
