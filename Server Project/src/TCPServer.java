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

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    public TCPServer() throws IOException {
        serverSocket = new ServerSocket(8081);

    }

    public static void main(String[] args) throws IOException {
        TCPServer server = new TCPServer();

        while(true) {
            //clientSocket = serverSocket.accept();
            Thread thread = new Thread(() -> server.handleRequests(server));
            thread.start();
        }
    }

    private void handleRequests(TCPServer server)  {

    }

    private void handlePutRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleDelRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleGetRequest(String clientSocketIP , int clientSocketPort) {}
    private void handleKeysRequest(String clientSocketIP , int clientSocketPort) {}

    private String getCurrentTimeStamp() {return null;}
    private String getLogHeader(String clientSocketIP , int clientSocketPort) { return null;}
}
