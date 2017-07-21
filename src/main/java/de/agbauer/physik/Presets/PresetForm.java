package de.agbauer.physik.Presets;

import javax.swing.*;

public class PresetForm {
    JPanel presetPanel;
    JButton lowMagButton;
    JButton apertureButton;
    JButton a1LensButton;
    JButton a2LensButton;
    JButton a3LensButton;
    JButton loadButton;
    JButton saveButton;
    JButton saveModeButton;

    void enablePresetPanel(boolean b) {
        lowMagButton.setEnabled(b);
        apertureButton.setEnabled(b);
        a1LensButton.setEnabled(b);
        a2LensButton.setEnabled(b);
        a3LensButton.setEnabled(b);
        loadButton.setEnabled(b);
        saveModeButton.setEnabled(b);
        saveButton.setEnabled(b);
    }
}
