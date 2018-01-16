package de.agbauer.physik.DelayStageServerCommunicator;

public enum DelayStageCommands {
    MOVE,
    MOVE_RELATIVE,
    GO_HOME,
    DEFINE_HOME,
    POSITION;

    public String cmdString() {
        switch (this) {
            case MOVE:
                return "MOV";
            case MOVE_RELATIVE:
                return "MVR";
            case GO_HOME:
                return "GOH";
            case DEFINE_HOME:
                return "DEH";
            case POSITION:
                return "POS";
            default:
                return "";
        }
    }


    public String displayName() {
        switch (this) {
            case MOVE:
                return "Move";
            case MOVE_RELATIVE:
                return "Move relative";
            case GO_HOME:
                return "Go Home";
            case DEFINE_HOME:
                return "Define Home";
            case POSITION:
                return "Position";
            default:
                return "";
        }
    }
}
