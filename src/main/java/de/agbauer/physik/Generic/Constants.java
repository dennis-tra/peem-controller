package de.agbauer.physik.Generic;

public class Constants {
    public static final String version = "v0.0.1";
    public static final boolean peemConnected = System.getenv("PEEM_CONNECTED") == "TRUE";
    public static final String defaultPort = "COM3";
    public static final String cameraDevice = System.getenv("CAMERA_DEVICE") == null ? "Camera" : System.getenv("CAMERA_DEVICE");
    public static final String defaultFileSaveFolder = "/Users/dennis/Documents/github/peem-controller/"; // Must have trailing (back)slash
    public static final String slackBotToken = "SLACK-TOKEN";
}
