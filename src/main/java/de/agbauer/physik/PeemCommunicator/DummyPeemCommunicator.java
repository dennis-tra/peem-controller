package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;

public class DummyPeemCommunicator implements PeemCommunicator {

    public void setProperty(PeemProperty property, Double value) throws IOException {

    }

    public String getProperty(PeemProperty property, PeemQuantity quantity) throws IOException {
        return "0.0";
    }
}
