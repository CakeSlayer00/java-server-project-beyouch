import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class TCPServer {
    private Socket clientSocket;
    private ServerSocket serverSocket;
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    private HashMap<String, String> keyValueStore;

    private final String QUIT = "QUIT";
    private final String KEYS = "KEYS";
    private final String PUT = "PUT";
    private final String DELETE = "DELETE";
    private final String GET = "GET";

    public TCPServer() throws IOException {
        serverSocket = new ServerSocket(8081);
    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();

    }

    private void handlePutRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleDelRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleGetRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleKeysRequest(String clientSocketIP , int clientSocketPort) {}

    private String getCurrentTimeStamp() {return null;}
    private String getLogHeader(String clientSocketIP , int clientSocketPort) { return null;}
}
