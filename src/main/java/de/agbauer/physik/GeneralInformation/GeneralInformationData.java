package de.agbauer.physik.GeneralInformation;

/**
 * Created by dennis on 17/02/2017.
 */
public class GeneralInformationData {
    public String sampleName;
    public String excitation;
    public String aperture;
    public String note;

    public boolean isValid() {
        return !empty(sampleName) && !empty(excitation);
    }

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
