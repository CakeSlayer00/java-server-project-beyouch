import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPClient {
    private HashMap<String , String > keyValStore;
    private int port;
    private InetAddress address;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket clientSocket;

    private byte[] sendData;
    private byte[] receiveData;

    private final String QUIT = "QUIT";
    private final String PUT = "PUT";
    private final String DELETE = "DELETE";
    private final String GET = "GET";

    public void sendThenReceive() {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))){
            String message = br.readLine();
            sendData = message.getBytes();

            sendPacket = new DatagramPacket(sendData, sendData.length, address, 1234);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
