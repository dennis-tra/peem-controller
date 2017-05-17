package de.agbauer.physik;

public class Constants {
    public static final boolean peemConnected = System.getenv("PEEM_CONNECTED") == "TRUE";
    public static final String version = "v0.2.0";
    public static final String defaultPort = "COM3";
    public static final String cameraDevice = System.getenv("CAMERA_DEVICE") == null ? "Camera" : System.getenv("CAMERA_DEVICE");
    public static final String defaultFileSaveFolder = "/Users/Hermann/Documents/peem_test/"; // Must have trailing (back)slash
    public static final String slackBotToken = "SLACK-TOKEN";
    public static final String defaultPresetSaveFolder = "/Users/Hermann/Documents/peem_test/presets/"; // Must have trailing (back)slash
}
