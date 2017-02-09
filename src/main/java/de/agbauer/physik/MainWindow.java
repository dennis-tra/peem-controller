package de.agbauer.physik;

import de.agbauer.physik.OptimisationSeries.OptimisationSeriesForm;
import de.agbauer.physik.PEEMCommunicator.RxTxConnectionHandler;
import gnu.io.CommPortIdentifier;

import javax.swing.*;
import java.util.List;

/**
 * Created by dennis on 09/02/2017.
 */
public class MainWindow extends JFrame {
    private JPanel rootPanel;
    JLabel statusBarLabel;
    OptimisationSeriesForm optimisationSeriesForm;

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

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}