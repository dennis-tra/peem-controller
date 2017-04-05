package de.agbauer.physik.QuickAcquisition;

import javax.swing.*;
import java.util.prefs.*;

public class SaveParameterDialog extends JDialog {

    private static final String SELECTED_APERTURE = "selected_aperture";
    private static final String SELECTED_EXCITATION = "selected_excitation";
    private static final String CURRENT_NOTE = "current_note";

    JPanel contentPane;
    JComboBox apertureComboBox;
    JTextField notesTextField;
    JTextField excitationTextField;

    public SaveParameterDialog() {

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        int selectedAperture = prefs.getInt(SELECTED_APERTURE, 500);

        apertureComboBox.addItem("30");
        apertureComboBox.addItem("70");
        apertureComboBox.addItem("150");
        apertureComboBox.addItem("500");
        apertureComboBox.addItem("1500");

        apertureComboBox.setSelectedItem("" + selectedAperture);

        String selectedExcitation = prefs.get(SELECTED_EXCITATION, "Hg");
        excitationTextField.setText(selectedExcitation);

        String currentNote = prefs.get(CURRENT_NOTE, "");
        notesTextField.setText(currentNote);
    }

    public static void main(String[] args) {
        SaveParameterDialog dialog = new SaveParameterDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    void saveSelectedParams() {
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        prefs.put(SELECTED_APERTURE, (String) apertureComboBox.getSelectedItem());
        prefs.put(SELECTED_EXCITATION, excitationTextField.getText());
        prefs.put(CURRENT_NOTE, notesTextField.getText());
    }
}
