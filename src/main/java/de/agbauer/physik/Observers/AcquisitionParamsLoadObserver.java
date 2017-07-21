package de.agbauer.physik.Observers;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import java.util.Observable;
import java.util.Observer;

public class AcquisitionParamsLoadObserver implements Observer {

    private AcquisitionParamsLoadListener[] listeners;

    public AcquisitionParamsLoadObserver(AcquisitionParamsLoadListener[] listeners){
        this.listeners = listeners;
    }

    @Override
    public void update(Observable observable, Object arg) {
        if(arg == null || arg instanceof PeemVoltages){
            PeemVoltages params = (PeemVoltages) arg;
            for (AcquisitionParamsLoadListener listener: listeners) {
                listener.peemVoltagesUpdated(observable, params);
            }
        } else if (arg instanceof String && arg == "setting-voltages-start") {
            for (AcquisitionParamsLoadListener listener: listeners) {
                listener.peemCommunicationStarted(observable);
            }
        } else if (arg instanceof String && arg == "setting-voltages-end") {
            for (AcquisitionParamsLoadListener listener: listeners) {
                listener.peemCommunicationFinished(observable);
            }
        }
    }
}
