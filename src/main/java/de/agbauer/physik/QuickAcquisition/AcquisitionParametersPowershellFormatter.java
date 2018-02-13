package de.agbauer.physik.QuickAcquisition;

import de.agbauer.physik.Constants;
import de.agbauer.physik.QuickAcquisition.AcquisitionParameters.AcquisitionParameters;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AcquisitionParametersPowershellFormatter implements AcquisitionParametersFormatter {

    public String format(AcquisitionParameters parameters) {
        String txtParams = "";
        txtParams += "Image Parameter file created by PEEM-Controller " + Constants.version + "\r\n";


        String runOn = new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date());
        txtParams += "Run on " + runOn + "\r\n\r\n";

        txtParams += String.format("%-16s %s%n", "ContrastAperture", parameters.generalData.aperture);
        txtParams += String.format("%-16s %s%n", "Excitation", parameters.generalData.excitation);
        txtParams += String.format("%-16s %s%n", "Sample", parameters.generalData.sampleName);
        txtParams += String.format("%-16s %s ms%n", "Integration", parameters.cameraData.exposureInMs);

        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        txtParams += String.format("%-16s %s%n", "Date", date);

        txtParams += String.format("%-16s %02d%n", "Imagenr", parameters.imageNumber);

        if (parameters.timeOffsetInFs != null) {
            txtParams += String.format("%-16s %.0fas%n", "timeOffset", parameters.timeOffsetInFs * 1000);
        }

        txtParams += String.format("%-16s %s%n", "Note", parameters.generalData.note);

        txtParams += "\r\n\r\nPEEM Parameters below. U is in V, I is in nA.\r\n\r\n";

        txtParams += String.format("%-11s %7s %6s%n", "Module", "U", "I");
        txtParams += String.format("%-11s %7s %6s%n", "------", "-", "-");
        txtParams += String.format("%-11s %7s %6s%n", "extractor", parameters.peemVoltages.extractor, parameters.peemCurrents.extractor);
        txtParams += String.format("%-11s %7s %6s%n", "focus", parameters.peemVoltages.focus, parameters.peemCurrents.focus);
        txtParams += String.format("%-11s %7s %6s%n", "column", parameters.peemVoltages.column, parameters.peemCurrents.column);
        txtParams += String.format("%-11s %7s %6s%n", "projective1", parameters.peemVoltages.projective1, parameters.peemCurrents.projective1);
        txtParams += String.format("%-11s %7s %6s%n", "projective2", parameters.peemVoltages.projective2, parameters.peemCurrents.projective2);
        txtParams += String.format("%-11s %7s %6s%n", "mcp", parameters.peemVoltages.mcp, parameters.peemCurrents.mcp);
        txtParams += String.format("%-11s %7s %6s%n", "screen", parameters.peemVoltages.screen, parameters.peemCurrents.screen);
        txtParams += "\r\n\r\n\r\n";

        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "Module", "Vx", "Vy", "Sx", "Sy");
        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "------", "--", "--", "--", "--");
        txtParams += String.format("%-9s %6s %6s %6s %6s%n", "stigmator", parameters.peemVoltages.stigmatorVx, parameters.peemVoltages.stigmatorVy,
                parameters.peemVoltages.stigmatorSx, parameters.peemVoltages.stigmatorSy);

        return txtParams;
    }
}
