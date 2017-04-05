package de.agbauer.physik.GeneralInformation;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Observable;

public class GeneralInformationController extends Observable implements DocumentListener {

    private final GeneralInformationForm form;

    public GeneralInformationController(GeneralInformationForm form) {
        this.form = form;
        this.form.sampleNameTextField.getDocument().addDocumentListener(this);
    }

    private void textFieldChanged() {
        notifyObservers();
    }

    @Override
    public void notifyObservers(Object arg) {
        String sampleName = this.form.sampleNameTextField.getText();
        setChanged();
        super.notifyObservers(sampleName);
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
