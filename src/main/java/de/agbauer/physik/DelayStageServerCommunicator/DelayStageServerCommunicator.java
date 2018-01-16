package de.agbauer.physik.DelayStageServerCommunicator;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

public class DelayStageServerCommunicator {
    private final Socket clientSocket;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private int TIMEOUT = 5000;

    private final DataOutputStream sendingStream;
    private final BufferedReader receivingStream;

    public DelayStageServerCommunicator(String host, int port) throws IOException {

        clientSocket = new Socket();
        clientSocket.setSoTimeout(TIMEOUT); // Timeout for read operations
        InetSocketAddress socketAddress = new InetSocketAddress("172.19.68.138", 2161);

        logger.info("Connecting to Delaystage Server...");
        clientSocket.connect(socketAddress, TIMEOUT);
        logger.info("Connection established!");


        sendingStream = new DataOutputStream(clientSocket.getOutputStream());
        receivingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "US-ASCII"));
    }

    public String sendCommand(DelayStageCommands command, int value) throws IOException {

        // Calculate length of Message and send the message length as four bytes before the actual message
        String message = command.cmdString() + Integer.toString(value);
        int messageLength = message.length();
        String lengthString = new String(ByteBuffer.allocate(4).putInt(messageLength).array());
        String commandStr = lengthString + message;


        logger.info("Sending '" + message + "' to Delaystage Server...");
        sendingStream.writeBytes(commandStr);
        sendingStream.flush();
        logger.info("Command Sent! Waiting for answer...");

        // Read first four bytes and calculate message length
        char[] chars = new char[4];
        int answerLength = receivingStream.read(chars, 0, 4);

        char[] answerArray = new char[answerLength];
        receivingStream.read(answerArray, 0, answerLength);
        String answer = new String(answerArray);

        logger.info("Delaystage Server answered: " + answer);

        return answer;
    }

    public void closeConnection() throws IOException {
        clientSocket.close();
    }
}
