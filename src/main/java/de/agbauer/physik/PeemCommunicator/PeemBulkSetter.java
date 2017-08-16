package de.agbauer.physik.PeemCommunicator;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.PeemVoltages;

import java.io.IOException;
import java.util.logging.Logger;

public class PeemBulkSetter {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private PeemCommunicator peemCommunicator;

    public PeemBulkSetter(PeemCommunicator peemCommunicator) {
        this.peemCommunicator = peemCommunicator;
    }

    public void setAllVoltages(PeemVoltages peemVoltages, boolean setStigmatorAndDeflector) throws IOException{
        logger.info("Setting all voltages...");
        PeemProperty[] peemProperties = PeemProperty.values();

        logger.info("Shutting down MCP for safety reasons...");
        this.peemCommunicator.setProperty(PeemProperty.MCP, 0.0);

        for (PeemProperty peemProperty: peemProperties) {
            if (peemProperty == PeemProperty.MCP) continue;
                if(!setStigmatorAndDeflector){
                    if (peemProperty == PeemProperty.STIGMATOR_X) continue;
                    if (peemProperty == PeemProperty.STIGMATOR_Y) continue;
                    if (peemProperty == PeemProperty.DEFLECTOR_X) continue;
                    if (peemProperty == PeemProperty.DEFLECTOR_Y) continue;
                }

            try {
                this.peemCommunicator.setProperty(peemProperty, peemVoltages.get(peemProperty));
            } catch (Exception e) {
                logger.warning("Error while setting " + peemProperty.toString() + ": " + e.getMessage());
            }
        }

    }
}
