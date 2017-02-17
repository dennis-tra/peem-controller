package de.agbauer.physik.Generic;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class AcquisitionParameterParser {

    public static AcquisitionParameters parse(File file) throws IOException {
        AcquisitionParameters ap = new AcquisitionParameters();

        Path path = Paths.get(file.getAbsolutePath());
        List<String> lines;
        lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (String line : lines) {
            ap = parseLine(ap, line);
        }

        return ap;
    }

    static AcquisitionParameters parseLine(AcquisitionParameters ap, String line) {

        try {
            if (line.startsWith("ContrastAperture")) {
                ap.aperture = parseGeneralInformationLine(line, "ContrastAperture");
            } else if (line.startsWith("Excitation")) {
                ap.excitation = parseGeneralInformationLine(line, "Excitation");
            } else if (line.startsWith("Sample")) {
                ap.sampleName = parseGeneralInformationLine(line, "Sample");
            } else if (line.startsWith("Integration")) {
                ap.exposure = parseGeneralInformationLine(line, "Integration");
            } else if (line.startsWith("Note")) {
                ap.note = parseGeneralInformationLine(line, "Note");
            } else if (line.startsWith("Imagenr")) {
                String imageNumberStr = parseGeneralInformationLine(line, "Imagenr");
                ap.imageNumber = Integer.parseInt(imageNumberStr);
            } else if (line.startsWith("Run on")) {
                String dateStr = parseGeneralInformationLine(line, "Run on");
                ap.createdAt = new SimpleDateFormat("yyyyMMdd-HHmm").parse(dateStr);
            } else if (line.startsWith("Date") && ap.createdAt == null) {
                String dateStr = parseGeneralInformationLine(line, "Date");
                ap.createdAt = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
            } else if (line.startsWith("extractor")) {
                ap.extractorU = getVoltageValue(line, "extractor");
                ap.extractorI = getCurrentValue(line, "extractor");
            } else if (line.startsWith("focus")) {
                ap.focusU = getVoltageValue(line, "focus");
                ap.focusI = getCurrentValue(line, "focus");
            } else if (line.startsWith("column")) {
                ap.columnU = getVoltageValue(line, "column");
                ap.columnI = getCurrentValue(line, "column");
            } else if (line.startsWith("projective1")) {
                ap.projective1U = getVoltageValue(line, "projective1");
                ap.projective1I = getCurrentValue(line, "projective1");
            } else if (line.startsWith("projective2")) {
                ap.projective2U = getVoltageValue(line, "projective2");
                ap.projective2I = getCurrentValue(line, "projective2");
            } else if (line.startsWith("mcp")) {
                ap.mcpU = getVoltageValue(line, "mcp");
                ap.mcpI = getCurrentValue(line, "mcp");
            } else if (line.startsWith("screen")) {
                ap.screenU = getVoltageValue(line, "screen");
                ap.screenI = getCurrentValue(line, "screen");
            } else if (line.startsWith("stigmator")) {
                String[] vals = line.substring("stigmator".length()).trim().replace(',', '.').split("\\s+");
                if (vals.length < 4) return ap;
                ap.stigmatorVx = Double.parseDouble(vals[0]);
                ap.stigmatorVy = Double.parseDouble(vals[1]);
                ap.stigmatorSx = Double.parseDouble(vals[2]);
                ap.stigmatorSy = Double.parseDouble(vals[3]);
            }
        } catch (NumberFormatException | ParseException e) {

        }
        return ap;
    }

    static private String parseGeneralInformationLine(String line, String key) {
        String sub = line.substring(key.length()).trim();
        return empty(sub) ? null : sub;
    }

    static private double getVoltageValue(String line, String key) {
        return getPEEMParameter(0, line, key);
    }

    static private double getCurrentValue(String line, String key) {
        return getPEEMParameter(1, line, key);
    }

    static private double getPEEMParameter(int index, String line, String key) {
        if (index > 1 || index < 0) {
            return 0.0;
        }

        String[] vals = line.substring(key.length()).trim().replace(',', '.').split("\\s+");
        if (vals.length < 2) {
            return 0.0;
        }
        String current = vals[index];
        if (empty(current)) {
            return 0.0;
        }
        return Double.parseDouble(current);
    }

    static private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
