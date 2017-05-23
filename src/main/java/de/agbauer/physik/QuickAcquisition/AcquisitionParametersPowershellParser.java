package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.*;
import de.agbauer.physik.PeemCommunicator.PeemProperty;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AcquisitionParametersPowershellParser implements AcquisitionParametersParser {

    private String sampleName;
    private String excitation;
    private String aperture;
    private String note;
    private Date createdAt;
    private float exposureInMs;
    private int imageNumber;

    private Map<PeemProperty, Double> peemVoltages = new HashMap<>();
    private Map<PeemProperty, Double> peemCurrents = new HashMap<>();

    public AcquisitionParameters parse(File file) throws IOException {

        Path path = Paths.get(file.getAbsolutePath());

        List<String> lines;
        lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        for (String line : lines) {
            this.parseLine(line);
        }

        GeneralAcquisitionData gad = new GeneralAcquisitionData(this.sampleName, this.excitation, this.aperture, this.note);
        PeemVoltages pv = new PeemVoltages(peemVoltages);
        PeemCurrents pc = new PeemCurrents(peemCurrents);
        CameraData cd = new CameraData(null, exposureInMs, 1);

        AcquisitionParameters ap = new AcquisitionParameters(gad, pv, pc, cd, this.createdAt);
        ap.imageNumber = this.imageNumber;

        return ap;
    }

    private void parseLine(String line) {

        try {
            if (line.startsWith("ContrastAperture")) {
                this.aperture = parseGeneralInformationLine(line, "ContrastAperture");
            } else if (line.startsWith("Excitation")) {
                this.excitation = parseGeneralInformationLine(line, "Excitation");
            } else if (line.startsWith("Sample")) {
                this.sampleName = parseGeneralInformationLine(line, "Sample");
            } else if (line.startsWith("Integration")) {
                this.exposureInMs = Float.parseFloat(parseGeneralInformationLine(line, "Integration"));
            } else if (line.startsWith("Note")) {
                this.note = parseGeneralInformationLine(line, "Note");
            } else if (line.startsWith("Imagenr")) {
                String imageNumberStr = parseGeneralInformationLine(line, "Imagenr");
                this.imageNumber = Integer.parseInt(imageNumberStr != null ? imageNumberStr : "1");
            } else if (line.startsWith("Run on")) {
                String dateStr = parseGeneralInformationLine(line, "Run on");
                this.createdAt = new SimpleDateFormat("yyyyMMdd-HHmm").parse(dateStr);
            } else if (line.startsWith("Date") && this.createdAt == null) {
                String dateStr = parseGeneralInformationLine(line, "Date");
                this.createdAt = new SimpleDateFormat("yyyyMMdd").parse(dateStr);
            } else if (line.startsWith("extractor")) {
                peemVoltages.put(PeemProperty.EXTRACTOR, getVoltageValue(line, "extractor"));
                peemCurrents.put(PeemProperty.EXTRACTOR, getCurrentValue(line, "extractor"));
            } else if (line.startsWith("focus")) {
                peemVoltages.put(PeemProperty.FOCUS, getVoltageValue(line, "focus"));
                peemCurrents.put(PeemProperty.FOCUS, getCurrentValue(line, "focus"));
            } else if (line.startsWith("column")) {
                peemVoltages.put(PeemProperty.COLUMN, getVoltageValue(line, "column"));
                peemCurrents.put(PeemProperty.COLUMN, getCurrentValue(line, "column"));
            } else if (line.startsWith("projective1")) {
                peemVoltages.put(PeemProperty.PROJECTIVE_1, getVoltageValue(line, "projective1"));
                peemCurrents.put(PeemProperty.PROJECTIVE_1, getCurrentValue(line, "projective1"));
            } else if (line.startsWith("projective2")) {
                peemVoltages.put(PeemProperty.PROJECTIVE_2, getVoltageValue(line, "projective2"));
                peemCurrents.put(PeemProperty.PROJECTIVE_2, getCurrentValue(line, "projective2"));
            } else if (line.startsWith("mcp")) {
                peemVoltages.put(PeemProperty.MCP, getVoltageValue(line, "mcp"));
                peemCurrents.put(PeemProperty.MCP, getCurrentValue(line, "mcp"));
            } else if (line.startsWith("screen")) {
                peemVoltages.put(PeemProperty.SCREEN, getVoltageValue(line, "screen"));
                peemCurrents.put(PeemProperty.SCREEN, getCurrentValue(line, "screen"));
            } else if (line.startsWith("stigmator")) {
                String[] vals = line.substring("stigmator".length()).trim().replace(',', '.').split("\\s+");
                if (vals.length < 4) return;
                peemVoltages.put(PeemProperty.DEFLECTOR_X, Double.parseDouble(vals[0]));
                peemVoltages.put(PeemProperty.DEFLECTOR_Y, Double.parseDouble(vals[1]));
                peemVoltages.put(PeemProperty.STIGMATOR_X, Double.parseDouble(vals[2]));
                peemVoltages.put(PeemProperty.STIGMATOR_Y, Double.parseDouble(vals[3]));
            }
        } catch (NumberFormatException | ParseException ignored) {

        }
    }

    static private String parseGeneralInformationLine(String line, String key) {
        String sub = line.substring(key.length()).trim();
        if (sub.endsWith(" ms")) {
            sub = sub.replace(" ms", "");
        }
        return empty(sub) ? null : sub;
    }

    static private double getVoltageValue(String line, String key) {
        return getPEEMParameter(0, line, key);
    }

    static private double getCurrentValue(String line, String key) {
        return getPEEMParameter(1, line, key);
    }

    static private double getPEEMParameter(int index, String line, String key) {
        assert index == 1 || index == 0;

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
