package de.agbauer.physik.DelayStageServerCommunicator;

import javax.swing.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.prefs.Preferences;


public class DelayStageConnectionHandler {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private static final String DELAY_STAGE_HOST = "DELAY_STAGE_HOST";
    private static final String DELAY_STAGE_PORT = "DELAY_STAGE_PORT";
    private int TIMEOUT = 5000;

    private final Socket clientSocket;

    private DataOutputStream sendingStream;
    private BufferedReader receivingStream;

    public DelayStageConnectionHandler() {
        clientSocket = new Socket();
    }

    public void disconnect() {
        try {
            clientSocket.close();
        } catch (IOException e) {
            logger.warning("Could not close connection to delay stage server: " + e.getMessage());
        }
    }

    public boolean isConnected() {
        return clientSocket.isConnected();
    }

    void connect() throws IOException {
        logger.info("Connecting to delay stage...");

        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        String savedHost = prefs.get(DELAY_STAGE_HOST, "172.19.68.138");
        int savedPort = prefs.getInt(DELAY_STAGE_PORT, 2161);

        while (true) {
            try {
                logger.info("Connection details - Host: " + savedHost + ":" + String.valueOf(savedPort));

                clientSocket.setSoTimeout(TIMEOUT); // Timeout for read operations

                InetSocketAddress socketAddress = new InetSocketAddress(savedHost, savedPort);

                clientSocket.connect(socketAddress, TIMEOUT);

                logger.info("Connection established!");

                sendingStream = new DataOutputStream(clientSocket.getOutputStream());
                receivingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),"US-ASCII"));
                break;
            } catch (IOException e) {
                logger.warning("Could not connect to delay stage server. Asking to enter new host and port.");

                DelayStageConnectionDialog connectionDialog = new DelayStageConnectionDialog(savedHost, savedPort);

                Object[] dialogOptions = { "Connect", "Cancel" };

                int result = JOptionPane.showOptionDialog(null,
                        connectionDialog.contentPane,
                        "Enter delay stage host and port",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        dialogOptions,
                        dialogOptions[0]);

                if (result == JOptionPane.YES_OPTION){
                    String newHost = connectionDialog.hostTextField.getText();
                    int newPort = Integer.parseInt(connectionDialog.portTextField.getText());

                    prefs.put(DELAY_STAGE_HOST, newHost);
                    prefs.putInt(DELAY_STAGE_PORT, newPort);

                } else {
                    throw new IOException("Could not connect to delay stage server");
                }
            }
        }
    }

    BufferedReader getReceivingStream() throws IOException {
        return this.receivingStream;
    }

    DataOutputStream getSendingStream() throws IOException {
        return this.sendingStream;
    }
}
