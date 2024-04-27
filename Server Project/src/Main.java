import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final int PORT = 12345;
    private final Map<String, String> keyValueStore;

    public Main() {
        keyValueStore = new HashMap<>();
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Key-Value Store Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String request = reader.readLine();
            String[] tokens = request.split(" ");

            if (tokens.length < 2) {
                writer.println("Invalid request format. Use: PUT key value, GET key, DELETE key, KEYS");
                return;
            }

            String operation = tokens[0];
            String key = tokens[1];

            switch (operation) {
                case "PUT":
                    if (tokens.length < 3) {
                        writer.println("Invalid PUT request. Use: PUT key value");
                        return;
                    }
                    String value = tokens[2];
                    keyValueStore.put(key, value);
                    writer.println("Stored key '" + key + "' with value '" + value + "'");
                    break;
                case "GET":
                    String storedValue = keyValueStore.get(key);
                    writer.println("Value for key '" + key + "': " + (storedValue != null ? storedValue : "Not found"));
                    break;
                case "DELETE":
                    keyValueStore.remove(key);
                    writer.println("Deleted key '" + key + "'");
                    break;
                case "KEYS":
                    writer.println("Keys in the store: " + String.join(", ", keyValueStore.keySet()));
                    break;
                default:
                    writer.println("Unknown operation: " + operation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Main server = new Main();
        server.start();
    }
}
