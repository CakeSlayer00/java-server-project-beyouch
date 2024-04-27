import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class TCPClient {
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private Socket clientSocket;

    private final String QUIT = "QUIT";
    private final String KEYS = "KEYS";
    private final String PUT = "PUT";
    private final String DELETE = "DELETE";
    private final String GET = "GET";

    public static void main(String[] args) {
        
    }

    private void cleanUp() {}
    private void handlePutRequest(String command , String key) {}
    private void handleDelRequest(String command , String key) {}
    private void handleGetRequest(String command , String key) {}
    private void handleKeysRequest(String command) {}

    private String getCurrentTimeStamp() {return null;}
}
