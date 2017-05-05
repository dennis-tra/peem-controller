package de.agbauer.physik.Observers;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersVoltages;

public interface AcquisitionParamsLoadListener {
    void loadParams(AcquisitionParametersVoltages params);
}
