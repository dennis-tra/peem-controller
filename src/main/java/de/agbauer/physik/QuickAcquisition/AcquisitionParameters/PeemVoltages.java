package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.io.Serializable;
import java.util.Map;

public class PeemVoltages implements Serializable {
    public final double extractor;
    public final double focus;
    public final double column;
    public final double projective1;
    public final double projective2;
    public final double mcp;
    public final double screen;
    public final double stigmatorVx;
    public final double stigmatorVy;
    public final double stigmatorSx;
    public final double stigmatorSy;

    public PeemVoltages(Map<PeemProperty, Double> allVoltages){
        this.extractor = allVoltages.get(PeemProperty.EXTRACTOR);
        this.focus = allVoltages.get(PeemProperty.FOCUS);
        this.column = allVoltages.get(PeemProperty.COLUMN);
        this.projective1 = allVoltages.get(PeemProperty.PROJECTIVE_1);
        this.projective2 = allVoltages.get(PeemProperty.PROJECTIVE_2);
        this.mcp = allVoltages.get(PeemProperty.MCP);
        this.screen = allVoltages.get(PeemProperty.SCREEN);
        this.stigmatorVx = allVoltages.get(PeemProperty.DEFLECTOR_X);
        this.stigmatorVy = allVoltages.get(PeemProperty.DEFLECTOR_Y);
        this.stigmatorSx = allVoltages.get(PeemProperty.STIGMATOR_X);
        this.stigmatorSy = allVoltages.get(PeemProperty.STIGMATOR_Y);
    }
}
