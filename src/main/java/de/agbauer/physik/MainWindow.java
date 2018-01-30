package de.agbauer.physik;

import de.agbauer.physik.DelayStageServerCommunicator.TimeResolvedForm;
import de.agbauer.physik.GeneralInformation.GeneralInformationForm;
import de.agbauer.physik.OptimisationSeries.OptimisationSeriesForm;
import de.agbauer.physik.PeemHistory.PEEMHistoryForm;
import de.agbauer.physik.PeemState.PEEMStateForm;
import de.agbauer.physik.Presets.PresetForm;
import de.agbauer.physik.QuickAcquisition.QuickAcquisitionForm;

import javax.swing.*;

public class MainWindow extends JFrame {
    private JPanel rootPanel;
    JLabel statusBarLabel;
    public OptimisationSeriesForm optimisationSeriesForm;
    PEEMStateForm peemStateForm;
    QuickAcquisitionForm quickAcquisitionForm;
    GeneralInformationForm generalInformationForm;
    PEEMHistoryForm peemHistoryForm;
    PresetForm presetForm;
    JButton browseButton;
    TimeResolvedForm timeResolvedForm;


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

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
