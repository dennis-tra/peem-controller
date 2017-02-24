package de.agbauer.physik.Logging;

import ij.IJ;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ImageJLogger extends Handler {
    @Override
    public void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        IJ.log(record.getMessage());
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
