package de.agbauer.physik.Logging;

import java.util.logging.Logger;


public class LogInitialiser {
    private Logger logger = null;

    public LogInitialiser(LabelLogHandler labelLogHandler) {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.addHandler(labelLogHandler);
        logger.addHandler(new SlackLogger());
    }
}
