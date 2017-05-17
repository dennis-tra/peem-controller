package de.agbauer.physik.QuickAcquisition;


import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.io.Serializable;
import java.util.Map;

public class AcquisitionParametersVoltages implements Serializable{
    public double extractorU;
    public double focusU;
    public double columnU;
    public double projective1U;
    public double projective2U;
    public double mcpU;
    public double screenU;
    public double stigmatorVx;
    public double stigmatorVy;
    public double stigmatorSx;
    public double stigmatorSy;

    public AcquisitionParametersVoltages(){

    }

    public AcquisitionParametersVoltages(Map<PeemProperty, String> allVoltages){
        this.extractorU = Double.parseDouble(allVoltages.get(PeemProperty.EXTRACTOR));
        this.focusU = Double.parseDouble(allVoltages.get(PeemProperty.FOCUS));
        this.columnU = Double.parseDouble(allVoltages.get(PeemProperty.COLUMN));
        this.projective1U = Double.parseDouble(allVoltages.get(PeemProperty.PROJECTIVE_1));
        this.projective2U = Double.parseDouble(allVoltages.get(PeemProperty.PROJECTIVE_2));
        this.mcpU = Double.parseDouble(allVoltages.get(PeemProperty.MCP));
        this.screenU = Double.parseDouble(allVoltages.get(PeemProperty.SCREEN));
    }
}
