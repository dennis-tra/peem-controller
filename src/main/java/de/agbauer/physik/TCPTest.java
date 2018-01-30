package de.agbauer.physik;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class TCPTest {
    public static void main(String argv[]) throws Exception {
        String sentence = "DEH0";
        int len = sentence.length();

        byte[] bytes = ByteBuffer.allocate(4).putInt(len).array();

        String lenStr = new String(bytes);
        System.out.println(lenStr+sentence);

        String modifiedSentence;
        System.out.println("Socket...");
        Socket clientSocket = new Socket("172.19.68.138", 2161);
        System.out.println("Socket established");

        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "US-ASCII"));

        System.out.println("Sending: " + lenStr + sentence);
        outToServer.writeBytes(lenStr + sentence);
        outToServer.flush();
        System.out.println("Sent!");

        char[] chars = new char[4];
        int messageLength = inFromServer.read(chars, 0, 4);

        char[] message = new char[messageLength];
        inFromServer.read(message, 0, messageLength);
        System.out.println("Antwort: " + new String(message));
        clientSocket.close();
    }
}
