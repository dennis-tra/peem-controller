package de.agbauer.physik.Logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


public class LogInitialiser {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public LogInitialiser(LabelLogger labelLogHandler) {
        logger.setUseParentHandlers(false);
        logger.addHandler(labelLogHandler);
        logger.addHandler(new SlackLogger());

        ImageJLogger imageJLogger = new ImageJLogger();
        imageJLogger.setFormatter(new IJFormatter());
        logger.addHandler(imageJLogger);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new CustomFormatter());
        logger.addHandler(consoleHandler);
    }
}
