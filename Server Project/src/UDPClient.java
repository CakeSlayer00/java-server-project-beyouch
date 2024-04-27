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

    private final String QUIT = "QUIT";
    private final String PUT = "PUT";
    private final String DELETE = "DELETE";
    private final String GET = "GET";

    private byte[] sendData;
    private byte[] receiveData;

    public static void main(String[] args) {

    }

    private void cleanUp() {}
    private void handlePutRequest(String command , String key) {}
    private void handleDelRequest(String command , String key) {}
    private void handleGetRequest(String command , String key) {}
    private String getCurrentTimeStamp() {return null;}
    private void sendDataPacket(String data) {}
    private String receiveDataPacket() {return null;}
}
