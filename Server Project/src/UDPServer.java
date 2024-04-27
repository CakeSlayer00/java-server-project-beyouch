import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;

public class UDPServer {
    private HashMap<String , String > keyValStore;
    private int port;
    private InetAddress address;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket serverSocket;

    private final String QUIT = "QUIT";
    private final String PUT = "PUT";
    private final String DELETE = "DELETE";
    private final String GET = "GET";

    private byte[] sendData;
    private byte[] receiveData;

    public static void main(String[] args) {

    }

    private void handlePutRequest() {}
    private void handleDelRequest() {}
    private void handleGetRequest() {}
    private String getLogHeader(String ip , int port) {return null;}
    private void sendDataPacket(String data) {}
    private String receiveDataPacket() {return null;}
}
