package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.GeneralInformation.GeneralInformationData;
import de.agbauer.physik.PEEMCommunicator.PEEMProperty;

import java.util.Date;
import java.util.Map;

public class AcquisitionParameters {
    public AcquisitionParameters() {

    }

    AcquisitionParameters(GeneralInformationData data, Map<PEEMProperty, String> allVoltages, Map<PEEMProperty, String> allCurrents) {
        this.aperture = data.aperture;
        this.excitation = data.excitation;
        this.sampleName = data.sampleName;
        this.note = data.note;

        //this.exposure = "";

        this.createdAt = new Date();

        //this.imageNumber = "";

        this.extractorU = Double.parseDouble(allVoltages.get(PEEMProperty.EXTRACTOR));
        this.focusU = Double.parseDouble(allVoltages.get(PEEMProperty.FOCUS));
        this.columnU = Double.parseDouble(allVoltages.get(PEEMProperty.COLUMN));
        this.projective1U = Double.parseDouble(allVoltages.get(PEEMProperty.PROJECTIVE_1));
        this.projective2U = Double.parseDouble(allVoltages.get(PEEMProperty.PROJECTIVE_2));
        this.mcpU = Double.parseDouble(allVoltages.get(PEEMProperty.MCP));
        this.screenU = Double.parseDouble(allVoltages.get(PEEMProperty.SCREEN));

        this.extractorI = Double.parseDouble(allCurrents.get(PEEMProperty.EXTRACTOR));
        this.focusI = Double.parseDouble(allCurrents.get(PEEMProperty.FOCUS));
        this.columnI = Double.parseDouble(allCurrents.get(PEEMProperty.COLUMN));
        this.projective1I = Double.parseDouble(allCurrents.get(PEEMProperty.PROJECTIVE_1));
        this.projective2I = Double.parseDouble(allCurrents.get(PEEMProperty.PROJECTIVE_2));
        this.mcpI = Double.parseDouble(allCurrents.get(PEEMProperty.MCP));
        this.screenI = Double.parseDouble(allCurrents.get(PEEMProperty.SCREEN));

        this.stigmatorVx = Double.parseDouble(allVoltages.get(PEEMProperty.DEFLECTOR_X));
        this.stigmatorVy = Double.parseDouble(allVoltages.get(PEEMProperty.DEFLECTOR_Y));
        this.stigmatorSx = Double.parseDouble(allVoltages.get(PEEMProperty.STIGMATOR_X));
        this.stigmatorSy = Double.parseDouble(allVoltages.get(PEEMProperty.STIGMATOR_Y));
    }

    public String aperture;
    public String excitation;
    public String sampleName;
    public String exposure;
    public Date createdAt;
    public int imageNumber;
    public String note;
    public double extractorU;
    public double extractorI;
    public double focusU;
    public double focusI;
    public double columnU;
    public double columnI;
    public double projective1U;
    public double projective1I;
    public double projective2U;
    public double projective2I;
    public double mcpU;
    public double mcpI;
    public double screenU;
    public double screenI;
    public double stigmatorVx;
    public double stigmatorVy;
    public double stigmatorSx;
    public double stigmatorSy;
}
