package de.agbauer.physik.Logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


public class LogInitialiser {
    private Logger logger = null;

    public LogInitialiser(LabelLogger labelLogHandler) {
        logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(labelLogHandler);
        logger.addHandler(new SlackLogger());

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomFormatter());
        logger.addHandler(consoleHandler);
    }
}
