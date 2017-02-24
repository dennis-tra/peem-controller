package de.agbauer.physik.PEEMCommunicator;

public enum PEEMQuantity {
    VOLTAGE,
    CURRENT;

    public String toString() {
        switch (this) {
            case CURRENT:
                return "I";
            case VOLTAGE:
                return "U";
            default:
                return "";
        }
    }
}
