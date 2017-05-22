package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.io.Serializable;
import java.util.AbstractList;
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

    public Double get(PeemProperty peemProperty) {
        switch (peemProperty) {
            case EXTRACTOR:
                return this.extractor;
            case FOCUS:
                return this.focus;
            case COLUMN:
                return this.column;
            case PROJECTIVE_1:
                return this.projective1;
            case PROJECTIVE_2:
                return this.projective2;
            case MCP:
                return this.mcp;
            case SCREEN:
                return this.screen;
            case DEFLECTOR_X:
                return this.stigmatorVx;
            case DEFLECTOR_Y:
                return this.stigmatorVy;
            case STIGMATOR_X:
                return this.stigmatorSx;
            case STIGMATOR_Y:
                return this.stigmatorSy;
            default:
                return null;
        }
    }

}
