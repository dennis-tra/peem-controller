package de.agbauer.physik.DelayStageServerCommunicator;

import com.google.protobuf.Internal;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.logging.Logger;

public class DelayStageServerCommunicator {
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final DataOutputStream sendingStream;
    private final BufferedReader receivingStream;

    public DelayStageServerCommunicator(DataOutputStream sendingStream, BufferedReader receivingStream) throws IOException {
        this.sendingStream = sendingStream;
        this.receivingStream = receivingStream;
    }

    String sendCommand(DelayStageCommands command, int value) throws IOException {

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
        receivingStream.read(chars, 0, 4);
        CharBuffer charBuffer = CharBuffer.wrap(chars);
        ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        int answerLength = byteBuffer.getInt();

        char[] answerArray = new char[answerLength];
        receivingStream.read(answerArray, 0, answerLength);
        String answer = new String(answerArray);

        logger.info("Delaystage Server answered: " + answer);

        return answer;
    }

    float moveRelativeBy(float femtoSeconds) throws IOException {

        int value = (int) (-femtoSeconds * 5000);

        String answer = sendCommand(DelayStageCommands.MOVE_RELATIVE, value);
        String position = answer.substring(3);

        return Float.parseFloat(position) / 5000;
    }

    float moveTo(double femtoSeconds) throws IOException {

        int value = (int) (femtoSeconds * 5000);

        String answer = sendCommand(DelayStageCommands.MOVE, value);
        String position = answer.substring(3);

        return Float.parseFloat(position) / 5000;
    }


    float getPosition() throws IOException {

        String answer = sendCommand(DelayStageCommands.POSITION, 0);
        String position = answer.substring(3);

        return Float.parseFloat(position) / 1000;
    }

    float defineHome() throws IOException {

        String answer = sendCommand(DelayStageCommands.DEFINE_HOME, 0);
        String position = answer.substring(3);

        // no idea whether 1000 is correct. Answer should always be POS0 anyway
        return Float.parseFloat(position) / 1000;
    }


    float goHome() throws IOException {

        String answer = sendCommand(DelayStageCommands.GO_HOME, 0);
        String position = answer.substring(3);

        // no idea whether 1000 is correct. Answer should always be POS0 anyway
        return Float.parseFloat(position) / 1000;
    }
}
