package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import java.util.Date;

public class AcquisitionParameters {

    public int imageNumber;
    public Double timeOffsetInFs;

    public final Date createdAt;

    public final PeemCurrents peemCurrents;
    public final PeemVoltages peemVoltages;
    public final GeneralAcquisitionData generalData;
    public final CameraData cameraData;

    public AcquisitionParameters(GeneralAcquisitionData data, PeemVoltages peemVoltages, PeemCurrents peemCurrents, CameraData cameraData) {
        this(data, peemVoltages, peemCurrents, cameraData, null);
    }

    public AcquisitionParameters(GeneralAcquisitionData data, PeemVoltages peemVoltages, PeemCurrents peemCurrents, CameraData cameraData, Date createdAt) {

        this.createdAt = createdAt == null ? new Date() : createdAt;

        this.generalData = data;
        this.peemVoltages = peemVoltages;
        this.peemCurrents = peemCurrents;
        this.cameraData = cameraData;
    }

}
