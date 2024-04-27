import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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

    public TCPClient() throws IOException {
        this.clientSocket = new Socket("localhost" , 8081);
        this.dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
        this.dataInputStream = new DataInputStream(clientSocket.getInputStream());
    }

    public static void main(String[] args) throws IOException {
        TCPClient client = new TCPClient();
        client.printAvailableCommands();
    }

    private void printAvailableCommands() {
        System.out.println("""
                Please Input Command in either of the following forms:
                \tGET <key>
                \tPUT <key> <val>
                \tDELETE <key>
                \tKEYS
                \tQUIT
                """);
    }

    private void cleanUp() {}
    private void handlePutRequest(String command , String key) {}
    private void handleDelRequest(String command , String key) {}
    private void handleGetRequest(String command , String key) {}
    private void handleKeysRequest(String command) {}

    private String getCurrentTimeStamp() {return null;}
}
