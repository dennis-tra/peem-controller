package de.agbauer.physik.Observers;

import java.util.Observable;
import java.util.Observer;

public class SampleNameChangeObserver implements Observer {

    private SampleNameChangeListener[] listeners;

    public SampleNameChangeObserver(SampleNameChangeListener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof String) {
            String data = (String) arg;
            for (SampleNameChangeListener listener: listeners) {
                listener.sampleNameChanged(data);
            }
        }
    }
}
