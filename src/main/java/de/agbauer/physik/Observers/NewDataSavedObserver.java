package de.agbauer.physik.Observers;

import ij.ImagePlus;

import java.util.Observable;
import java.util.Observer;

public class NewDataSavedObserver implements Observer{

    private DataSaveListeners[] listeners;

    public NewDataSavedObserver(DataSaveListeners[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof ImagePlus) {
            ImagePlus image = (ImagePlus) arg;
            for (DataSaveListeners listener: listeners) {
                listener.newDataSaved(image);
            }
        } else {
            for (DataSaveListeners listener: listeners) {
                listener.newDataSaved(null);
            }
        }
    }
}
