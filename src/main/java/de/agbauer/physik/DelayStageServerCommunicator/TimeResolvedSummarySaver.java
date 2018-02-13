package de.agbauer.physik.DelayStageServerCommunicator;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;
import de.agbauer.physik.QuickAcquisition.AcquisitionParametersPowershellFormatter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

class TimeResolvedSummarySaver {
    static void startSummary(String filePath, AcquisitionParameters ap) throws FileNotFoundException {
        AcquisitionParametersPowershellFormatter appf = new AcquisitionParametersPowershellFormatter();

        try (PrintStream out = new PrintStream(new FileOutputStream(filePath))) {
            String txtParams = appf.format(ap);

            txtParams += "\r\n\r\n";
            txtParams += "Starting time resolved measurement:\r\n";

            out.print(txtParams);
        }
    }

    static void append(String filePath, TimeResolvedParameters trp, int idx) throws FileNotFoundException {
        String txtParams = "";

        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        txtParams += String.format("#%03d %s - %.0f as\r\n", idx + 1, now, trp.values.get(idx) * 1000);

        try (PrintStream out = new PrintStream(new FileOutputStream(filePath, true))) {
            out.print(txtParams);
        }

    }
}
