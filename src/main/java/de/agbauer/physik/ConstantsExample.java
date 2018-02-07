package de.agbauer.physik;

public class ConstantsExample {
    public static final boolean peemConnected = System.getenv("PEEM_CONNECTED") == "TRUE";
    public static final String version = "v0.5.0";
    public static final String defaultPort = "COM3";
    public static final String cameraDevice = "Camera";
    public static final String defaultFileSaveFolder = "/Users/dennis/Documents/github/peem-controller/"; // Must have trailing (back)slash
    public static final String slackBotToken = "xoxb-164035809249-fabo7Y9Ojj7GgreNzfbZGRCp";
    public static final String defaultPresetSaveFolder = "/Users/dennis/Documents/github/peem-controller/peem-controller-presets/"; // Must have trailing (back)slash
    public static final String defaultLogFolder = "/Users/dennis/Documents/github/peem-controller/peem-controller-logs/"; // Must have trailing (back)slash
    public static final String pathSeparator = "/";
}
