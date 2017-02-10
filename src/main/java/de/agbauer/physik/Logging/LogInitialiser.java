package de.agbauer.physik.Logging;

import java.util.logging.Logger;


public class LogInitialiser {
    private Logger logger = null;

    public LogInitialiser(LabelLogHandler labelLogHandler) {
        logger = Logger.getLogger("peem-logging.handler");
        logger.addHandler(labelLogHandler);
    }
}
