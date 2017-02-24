package de.agbauer.physik.PEEMCommunicator;

public enum PEEMProperty {
    COLUMN,
    MCP,
    SCREEN,
    PROJECTIVE_1,
    PROJECTIVE_2,
    DEFLECTOR_X,
    DEFLECTOR_Y,
    FOCUS,
    STIGMATOR_X,
    STIGMATOR_Y,
    EXTRACTOR;

    public String cmdString() {
        switch (this) {
            case EXTRACTOR:
                return "extractor";
            case FOCUS:
                return "focus";
            case STIGMATOR_X:
                return "stigmator Sx";
            case STIGMATOR_Y:
                return "stigmator Sy";
            case COLUMN:
                return "column";
            case DEFLECTOR_X:
                return "stigmator Vx";
            case DEFLECTOR_Y:
                return "stigmator Vy";
            case MCP:
                return "mcp";
            case PROJECTIVE_1:
                return "projective1";
            case PROJECTIVE_2:
                return "projective2";
            case SCREEN:
                return "screen";
            default:
                return "";
        }
    }

    public String setCmdString() {
        switch (this) {
            case EXTRACTOR:
            case FOCUS:
            case COLUMN:
            case MCP:
            case PROJECTIVE_1:
            case PROJECTIVE_2:
            case SCREEN:
                return cmdString() + " U";
            case STIGMATOR_X:
            case STIGMATOR_Y:
            case DEFLECTOR_X:
            case DEFLECTOR_Y:
                return cmdString();
            default:
                return "";
        }
    }

    public String displayName() {
        switch (this) {
            case EXTRACTOR:
                return "Extractor";
            case FOCUS:
                return "Focus";
            case STIGMATOR_X:
                return "Sx";
            case STIGMATOR_Y:
                return "Sy";
            case COLUMN:
                return "Column";
            case DEFLECTOR_X:
                return "Vx";
            case DEFLECTOR_Y:
                return "Vy";
            case MCP:
                return "MCP";
            case PROJECTIVE_1:
                return "P1";
            case PROJECTIVE_2:
                return "P2";
            case SCREEN:
                return "Screen";
            default:
                return "";
        }
    }
}
