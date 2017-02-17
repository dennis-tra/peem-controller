package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Generic.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dennis on 17/02/2017.
 */
public class AcquisitionParametersFormatter {
    private AcquisitionParameters parameters;

    AcquisitionParametersFormatter(AcquisitionParameters parameters) {
        this.parameters = parameters;
    }

    String format() {
        String txtParams = "";
        txtParams += "Image Parameter file created by PEEM-Controller " + Constants.version + "\n";


        String runOn = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());
        txtParams += "Run on " + runOn + "\n\n";

        txtParams += String.format("%-16s %s%n", "ContrastAperture", parameters.aperture);
        txtParams += String.format("%-16s %s%n", "Excitation", parameters.excitation);
        txtParams += String.format("%-16s %s%n", "Sample", parameters.sampleName);
        txtParams += String.format("%-16s %s ms%n", "Integration", parameters.exposure);

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        txtParams += String.format("%-16s %s%n", "Date", date);

        txtParams += String.format("%-16s %02d%n", "Imagenr", parameters.imageNumber);
        txtParams += String.format("%-16s %s%n", "Note", parameters.note == null ? "": parameters.note);

        txtParams += "\n\nPEEM Parameters below. U is in V, I is in nA.\n\n";

        txtParams += String.format("%-11s %7s %6s%n", "Module", "U", "I");
        txtParams += String.format("%-11s %7s %6s%n", "------", "-", "-");
        txtParams += String.format("%-11s %7s %6s%n", "extractor", parameters.extractorU, parameters.extractorI);
        txtParams += String.format("%-11s %7s %6s%n", "focus", parameters.focusU, parameters.focusI);
        txtParams += String.format("%-11s %7s %6s%n", "column", parameters.columnU, parameters.columnI);
        txtParams += String.format("%-11s %7s %6s%n", "projective1", parameters.projective1U, parameters.projective1I);
        txtParams += String.format("%-11s %7s %6s%n", "projective2", parameters.projective2U, parameters.projective2I);
        txtParams += String.format("%-11s %7s %6s%n", "mcp", parameters.mcpU, parameters.mcpI);
        txtParams += String.format("%-11s %7s %6s%n", "screen", parameters.screenU, parameters.screenI);
        txtParams += "\n\n\n";

        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "Module", "Vx", "Vy", "Sx", "Sy");
        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "------", "--", "--", "--", "--");
        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "stigmator", parameters.stigmatorVx, parameters.stigmatorVy, parameters.stigmatorSx, parameters.stigmatorSy);

        return txtParams;
    }
}
