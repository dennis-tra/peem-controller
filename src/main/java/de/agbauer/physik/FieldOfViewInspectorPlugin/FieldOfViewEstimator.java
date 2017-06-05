package de.agbauer.physik.FieldOfViewInspectorPlugin;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan;

public class FieldOfViewEstimator {
    /* The projective voltage curve for p1 and p2 in the 2-lens (bachelor thesis Tim Jacobsen) mode has been
       fittet with an arctan function (no special reason, the arctan just fits the curve). The fit function
       can now be used to determine, whether the PEEM is used in the 2-lens mode.*/
    private static final double I_0_arctan_2Lens = -3.68192966e+02;
    private static final double x_0_arctan_2Lens = 3.14036931e+02;
    private static final double a_arctan_2Lens = 9.91193274e-03;
    private static final double offset_arctan_2Lens = 6.05730497e+02;

    /* The FOV has also been fitted as a function of p1 in order to estimate the FOV. p1 < 400V has been
    *  fitted with a parabole, p > 400V with a line*/
    private static final double a_FOV_left_2Lens = 4.55215215e-04;
    private static final double b_FOV_left_2Lens = 1.43054802e-02;
    private static final double c_FOV_left_2Lens = 1.28043227e+01;

    private static final double a_FOV_right_2Lens = -5.88062026e-02;
    private static final double b_FOV_right_2Lens = 1.11988929e+02;

    //The Fitparamters for the 3Lens mode (the curve of the projective voltages is a line)
    private static final double a_Line_3Lens = 0.25;
    private static final double b_Line_3Lens = 0;

    //The Fitparameters of the FOV of the 3Lens mode (parabole shaped)
    private static final double a_FOV_3Lens = 9.40698294e-04;
    private static final double b_FOV_3Lens = -1.19736141e-01;
    private static final double c_FOV_3Lens = 1.15485075e+01;

    private static final double tolerance_epsilon = 60;

    //Estimates the FOV (assuming that the extractor voltage is around 12.5kV!)
    public static double estimateFieldOfView(double p1, double p2, double u_extract){
        if (u_extract >= 9500) {
            if (p1 > 10 && p2 > 10) {
                if (isInTwoLensMode(p1, p2)) {
                    if (p1 <= 400) {
                        return a_FOV_left_2Lens * p1 * p1 + b_FOV_left_2Lens * p1 + c_FOV_left_2Lens;
                    } else {
                        return a_FOV_right_2Lens * p1 + b_FOV_right_2Lens;
                    }
                }

                if (isInThreeLensMode(p1, p2)) {
                    return a_FOV_3Lens * p1 * p1 + b_FOV_3Lens * p1 + c_FOV_3Lens;
                }

                //Not enough data is available for the consideration of different extractor voltages
                if (isInOneLensMode(p1, p2)) {
                    return 145.0;
                }
            }
        }
        return -1;
    }

    private static boolean isInThreeLensMode(double p1, double p2){
        double p2_est = p1*a_Line_3Lens+b_Line_3Lens;
        return (abs(p2_est - p2) <= tolerance_epsilon);
    }

    //Returns true if the PEEM voltage p2 is within the tolerance around the function value
    //(arctan fitted in python)
    private static boolean isInTwoLensMode(double p1, double p2){
        double p2_est  = I_0_arctan_2Lens*(2/PI)*atan(a_arctan_2Lens*(PI/2)*(p1-x_0_arctan_2Lens)) + offset_arctan_2Lens;
        return (abs(p2_est - p2) <= tolerance_epsilon);
    }

    //In the one-lens mode both voltages are usually 1000V (column voltage)
    private static boolean isInOneLensMode(double p1, double p2){
        return (abs(p1 - 1000) <= tolerance_epsilon) && (abs(p2 - 1000) <= tolerance_epsilon);
    }
}

