package de.agbauer.physik.PEEMCommunicator;

import de.agbauer.physik.Generic.LogManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dennis on 10/02/2017.
 */
public class PEEMBulkReader {
    LogManager logManager;
    PEEMCommunicator peemCommunicator;

    public PEEMBulkReader(PEEMCommunicator peemCommunicator, LogManager logManager) {
        this.peemCommunicator = peemCommunicator;
        this.logManager = logManager;
    }


    public Map<PEEMProperty, String> getAllVoltages() throws IOException {
        logManager.inform("Reading all voltages...", true, true);
        return getAllPropertiesForQuantity(PEEMQuantity.VOLTAGE);
    }

    public Map<PEEMProperty, String> getAllCurrents() throws IOException{
        logManager.inform("Reading all currents...", true, true);
        return getAllPropertiesForQuantity(PEEMQuantity.CURRENT);
    }

    private Map<PEEMProperty, String> getAllPropertiesForQuantity(PEEMQuantity quantity) throws IOException {

        PEEMProperty[] peemProperties = PEEMProperty.values();

        Map<PEEMProperty, String> map = new HashMap<PEEMProperty, String>();
        for (PEEMProperty property: peemProperties) {
            String val = peemCommunicator.getProperty(property, quantity);
            map.put(property, val);
        }

        return map;
    }
}
