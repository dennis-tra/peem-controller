package de.agbauer.physik.PEEMCommunicator;

/**
 * Created by dennis on 02/02/2017.
 */
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
