package de.agbauer.physik.PEEMCommunicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by dennis on 10/02/2017.
 */
public class PEEMBulkReader {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    PEEMCommunicator peemCommunicator;

    public PEEMBulkReader(PEEMCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }


    public Map<PEEMProperty, String> getAllVoltages() throws IOException {
        logger.info("Reading all voltages...");
        return getAllPropertiesForQuantity(PEEMQuantity.VOLTAGE);
    }

    public Map<PEEMProperty, String> getAllCurrents() throws IOException{
        logger.info("Reading all currents...");
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
