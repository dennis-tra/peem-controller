package de.agbauer.physik.PeemCommunicator;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemCurrents;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

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

    public PeemVoltages getAllVoltages() throws IOException {
        logger.info("Reading all peemVoltages...");
        return new PeemVoltages(getAllPropertiesForQuantity(PeemQuantity.VOLTAGE));
    }

    public PeemCurrents getAllCurrents() throws IOException{
        logger.info("Reading all currents...");
        return new PeemCurrents(getAllPropertiesForQuantity(PeemQuantity.CURRENT));
    }

    private Map<PeemProperty, Double> getAllPropertiesForQuantity(PeemQuantity quantity) throws IOException {

        PeemProperty[] peemProperties = PeemProperty.values();

        Map<PeemProperty, Double> map = new HashMap<>();
        for (PeemProperty property: peemProperties) {
            String val = peemCommunicator.getProperty(property, quantity);
            map.put(property, Double.parseDouble(val));
        }

        return map;
    }
}
