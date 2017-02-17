package de.agbauer.physik.Logging;

import de.agbauer.physik.Generic.Constants;

import javax.swing.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class LabelLogger extends Handler {
    private JLabel label;

    public LabelLogger(JLabel label) {
        this.label = label;
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }
        label.setText(record.getMessage());
    }

    @Override
    public void flush() {
        label.setText("PEEM Controller " + Constants.version);
    }

    @Override
    public void close() throws SecurityException {

    }


}
