package de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.io.Serializable;
import java.util.Map;

public class PeemCurrents implements Serializable {
    public final double extractor;
    public final double focus;
    public final double column;
    public final double projective1;
    public final double projective2;
    public final double mcp;
    public final double screen;

    public PeemCurrents(Map<PeemProperty, Double> allCurrents){
        this.extractor = allCurrents.get(PeemProperty.EXTRACTOR);
        this.focus = allCurrents.get(PeemProperty.FOCUS);
        this.column = allCurrents.get(PeemProperty.COLUMN);
        this.projective1 = allCurrents.get(PeemProperty.PROJECTIVE_1);
        this.projective2 = allCurrents.get(PeemProperty.PROJECTIVE_2);
        this.mcp = allCurrents.get(PeemProperty.MCP);
        this.screen = allCurrents.get(PeemProperty.SCREEN);
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
            default:
                return null;
        }
    }
}
