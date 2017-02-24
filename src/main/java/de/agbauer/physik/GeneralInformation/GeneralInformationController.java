package de.agbauer.physik.GeneralInformation;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Observable;

public class GeneralInformationController extends Observable implements DocumentListener {

    private final GeneralInformationForm form;

    private String sampleName = "";
    private String excitation = "";
    private String note = "";

    public GeneralInformationController(GeneralInformationForm form) {
        this.form = form;
        this.form.sampleNameTextField.getDocument().addDocumentListener(this);
        this.form.excitationTextField.getDocument().addDocumentListener(this);
        this.form.noteTextField.getDocument().addDocumentListener(this);
    }

    private void textFieldChanged() {
        sampleName = this.form.sampleNameTextField.getText();
        excitation = this.form.excitationTextField.getText();
        note = this.form.noteTextField.getText();
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        GeneralInformationData data = new GeneralInformationData();
        data.excitation = this.excitation;
        data.sampleName = this.sampleName;
        data.note = this.note;
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

}
