package de.agbauer.physik.GeneralInformation;

public class GeneralInformationData {
    public String sampleName;
    public String excitation;
    public String aperture;
    public String note;

    private boolean empty( final String s ) {
        return s == null || s.trim().isEmpty();
    }

}
