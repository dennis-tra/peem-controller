package de.agbauer.physik.GeneralInformation;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Observable;

public class GeneralInformationController extends Observable implements DocumentListener {

    private final GeneralInformationForm form;

    public String sampleName = "";
    public String excitation = "";

    public GeneralInformationController(GeneralInformationForm form) {
        this.form = form;
        this.form.probeNameTextField.getDocument().addDocumentListener(this);
        this.form.excitationTextField.getDocument().addDocumentListener(this);
    }

    private void textFieldChanged() {
        sampleName = this.form.probeNameTextField.getText();
        excitation = this.form.excitationTextField.getText();
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        GeneralInformationData data = new GeneralInformationData();
        data.excitation = this.excitation;
        data.sampleName = this.sampleName;
        data.aperture = (String) this.form.apertureComboBox.getSelectedItem();

        setChanged();
        super.notifyObservers(data);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        textFieldChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textFieldChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {

    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
