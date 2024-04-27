import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class TestTCPServer {
    public static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8081);
        System.out.println("Server started. Listening for Clients on port 8081");

        while (true) {
            Socket socket = serverSocket.accept();
            printSuccessfulConnectionMessage(socket);
            Thread thread = new Thread(new ClientHandler(socket));
            thread.start();
        }
    }

    private static void printSuccessfulConnectionMessage(Socket socket) {
        System.out.printf("[%s] Client[%s] - Client Connection Successful!.\n", LocalDateTime.now().format(timeFormatter), socket.getRemoteSocketAddress());
    }
}

class ClientHandler implements Runnable {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private HashMap<String, String> keyValueStore;

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        keyValueStore = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            String request = inputStream.readUTF();
            String[] parts = request.split(" ");
            String command = parts[0];
            String key = parts.length > 1 ? parts[1] : null;
            String value = parts.length > 2 ? parts[2] : null;

            handleRequest(command, key, value);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleRequest(String command, String key, String value) throws IOException {
        switch (command) {
            case GET -> handleGetRequest(key);
            case PUT -> handlePutRequest(key, value);
            case DELETE -> handleDeleteRequest(key);
            case KEYS -> returnAllKeys();
            case QUIT -> socket.close();
            default -> printDefaultMessage();
        }
    }

    private void printDefaultMessage() throws IOException {
        System.out.printf("\u001B[31m" + "[%s] Client[%s] - Wrong format of command.\n" + "\u001B[0m", LocalDateTime.now().format(TestTCPServer.timeFormatter), socket.getRemoteSocketAddress());
        outputStream.writeUTF(String.format("\u001B[31m" + "[%s] Wrong format of command.\n" + "\u001B[0m" , LocalDateTime.now().format(TestTCPServer.timeFormatter)));
    }

    private void returnAllKeys() {
        String res = "";
        if (!keyValueStore.keySet().isEmpty()) {
            StringBuilder builder = new StringBuilder("Success: Keys - ");
            for (String key : keyValueStore.keySet()) {
                builder.append(key).append(" ,");
            }
            res = builder.substring(0, builder.length() - 1).trim();
        }

        try {
            if (!res.isEmpty())
                outputStream.writeUTF(res);
            else {
                String noKeysMessage = "Success: Keys - There is no keys";
                outputStream.writeUTF(noKeysMessage);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void printKeyDoesNotExist(String key) throws IOException {
        System.out.printf("\u001B[31m" + "[%s] Error: [Err] The key \"%s\" does not exists in the store\n" + "\u001B[0m", LocalDateTime.now().format(TestTCPServer.timeFormatter), key);
        outputStream.writeUTF(String.format("\u001B[31m" + "[%s] Error: [Err] The key \"%s\" does not exists in the store\n" + "\u001B[0m", LocalDateTime.now().format(TestTCPServer.timeFormatter), key));
    }

    private void printKeyDeletedSuccessfully(String key) throws IOException {
        System.out.printf("[%s] Success: The key \"%s\" deleted successfully\n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key);
        outputStream.writeUTF(String.format("[%s] Success: The key \"%s\" deleted successfully\n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key));
    }

    private void handleDeleteRequest(String key) throws IOException {
        if (keyValueStore.containsKey(key)) {
            keyValueStore.remove(key);
            printKeyDeletedSuccessfully(key);
        } else {
            printKeyDoesNotExist(key);
        }
    }

    private void printEntryPlacedSuccessfully(String key , String value) throws IOException {
        System.out.printf("[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key , value);
        outputStream.writeUTF(String.format("[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key , value));
    }

    private void handlePutRequest(String key, String value) throws IOException {
        if (!keyValueStore.containsKey(key)) {
            keyValueStore.put(key, value);
            printEntryPlacedSuccessfully(key , value);
        }
    }

    private void printEntryDoesNotExist(String key) throws IOException {
        outputStream.writeUTF(String.format("\u001B[31m" + "[%s] The value under the key \"%s\" does not exists.\n" + "\u001B[0m" , LocalDateTime.now().format(TestTCPServer.timeFormatter) , key));
    }

    private void handleGetRequest(String key) throws IOException {
        if (keyValueStore.containsKey(key)) {
            System.out.printf("[%s] Success: The value under the key \"%s\" is - %s\n%n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key , keyValueStore.get(key));
            outputStream.writeUTF(String.format("[%s] Success: The value under the key \"%s\" is - %s\n", LocalDateTime.now().format(TestTCPServer.timeFormatter), key , keyValueStore.get(key)));
        } else {
            printEntryDoesNotExist(key);
        }
    }
}
