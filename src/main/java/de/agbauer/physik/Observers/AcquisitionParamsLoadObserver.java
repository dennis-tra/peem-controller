package de.agbauer.physik.Observers;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import java.util.Observable;
import java.util.Observer;

public class AcquisitionParamsLoadObserver implements Observer{

    private AcquisitionParamsLoadListener[] listeners;

    public AcquisitionParamsLoadObserver(AcquisitionParamsLoadListener[] listeners){
        this.listeners = listeners;
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof PeemVoltages){
            PeemVoltages params = (PeemVoltages) arg;
            for (AcquisitionParamsLoadListener listener: listeners) {
                listener.loadParams(params);
            }
        }
    }
}
