package de.agbauer.physik;

public class Constants {
    public static final boolean peemConnected = System.getenv("PEEM_CONNECTED") == "TRUE";
    public static final String version = "v0.2.0";
    public static final String defaultPort = "COM3";
    public static final String cameraDevice = System.getenv("CAMERA_DEVICE") == null ? "Camera" : System.getenv("CAMERA_DEVICE");
    public static final String defaultFileSaveFolder = "/Users/dennis/Documents/github/peem-controller/"; // Must have trailing (back)slash
    public static final String slackBotToken = "SLACK-TOKEN";
    public static final String defaultPresetSaveFolder = "/Users/dennis/Documents/github/peem-controller/presets/"; // Must have trailing (back)slash
    public static final String defaultLogFolder = "/Users/dennis/Documents/github/peem-controller/logs/"; // Must have trailing (back)slash
}
