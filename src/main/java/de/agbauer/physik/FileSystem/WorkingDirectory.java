package de.agbauer.physik.FileSystem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkingDirectory {

    public static String getCurrentDirectory(String sampleName) {

        SimpleDateFormat date = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthName = new SimpleDateFormat("MMMM", Locale.GERMAN);
        SimpleDateFormat monthNumber = new SimpleDateFormat("MM");

        Date now = new Date();

        String dateStr = date.format(now);
        String yearStr = year.format(now);
        String monthNameStr = monthName.format(now);
        String monthNumberStr = monthNumber.format(now);

        return yearStr + File.separator +
                monthNumberStr + "_" + monthNameStr + File.separator +
                dateStr + "_" + sampleName + File.separator;

    }
}
