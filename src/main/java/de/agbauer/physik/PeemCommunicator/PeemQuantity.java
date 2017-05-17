package de.agbauer.physik.PeemCommunicator;

public enum PeemQuantity {
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
