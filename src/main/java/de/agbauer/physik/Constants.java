package de.agbauer.physik;

public class Constants {
    public static final boolean peemConnected = System.getenv("PEEM_CONNECTED") == "TRUE";
    public static final String version = "v0.5.0";
    public static final String defaultPort = "COM3";
    public static final String cameraDevice = "pco_camera";
    public static final String defaultFileSaveFolder = "F:\\PEEM Messdaten\\"; // Must have trailing (back)slash
    public static final String slackBotToken = "xoxb-164035809249-fabo7Y9Ojj7GgreNzfbZGRCp";
    public static final String defaultPresetSaveFolder = "F:\\PEEM Messdaten\\peem-controller-presets\\"; // Must have trailing (back)slash
    public static final String defaultLogFolder = "F:\\PEEM Messdaten\\peem-controller-logs\\"; // Must have trailing (back)slash
    public static final String pathSeparator = "\\";
}
