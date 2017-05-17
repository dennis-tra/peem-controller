package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class PeemBulkReader {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private PeemCommunicator peemCommunicator;

    public PeemBulkReader(PeemCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }


    public Map<PeemProperty, String> getAllVoltages() throws IOException {
        logger.info("Reading all voltages...");
        return getAllPropertiesForQuantity(PeemQuantity.VOLTAGE);
    }

    public Map<PeemProperty, String> getAllCurrents() throws IOException{
        logger.info("Reading all currents...");
        return getAllPropertiesForQuantity(PeemQuantity.CURRENT);
    }

    private Map<PeemProperty, String> getAllPropertiesForQuantity(PeemQuantity quantity) throws IOException {

        PeemProperty[] peemProperties = PeemProperty.values();

        Map<PeemProperty, String> map = new HashMap<PeemProperty, String>();
        for (PeemProperty property: peemProperties) {
            String val = peemCommunicator.getProperty(property, quantity);
            map.put(property, val);
        }

        return map;
    }
}
