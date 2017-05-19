package de.agbauer.physik.Observers;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

public interface AcquisitionParamsLoadListener {
    void loadParams(PeemVoltages params);
}
