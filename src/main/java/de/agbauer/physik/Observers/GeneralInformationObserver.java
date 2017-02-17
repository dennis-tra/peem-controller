package de.agbauer.physik.Observers;

import de.agbauer.physik.GeneralInformation.GeneralInformationChangeListener;
import de.agbauer.physik.GeneralInformation.GeneralInformationData;

import java.util.Observable;
import java.util.Observer;

public class GeneralInformationObserver implements Observer {

    private GeneralInformationChangeListener[] listeners;

    public GeneralInformationObserver(GeneralInformationChangeListener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof GeneralInformationData) {
            GeneralInformationData data = (GeneralInformationData) arg;
            for (GeneralInformationChangeListener listener: listeners) {
                listener.generalInformationChanged(data);
            }
        }
    }
}
