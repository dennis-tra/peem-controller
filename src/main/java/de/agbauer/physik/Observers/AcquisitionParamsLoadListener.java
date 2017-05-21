package de.agbauer.physik.Observers;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import java.util.Observable;

public interface AcquisitionParamsLoadListener {
    void peemVoltagesUpdated(Observable sender, PeemVoltages peemVoltages);
}
