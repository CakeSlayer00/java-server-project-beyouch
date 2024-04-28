import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

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
    public static final String ASCII_RED = "\u001B[31m";
    public static final String ASCII_COLOR_RESET = "\u001B[0m";

    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private HashMap<String, String> keyValueStore;

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    private ArrayList<Color> colors = new ArrayList<Color>();

    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
        keyValueStore = new HashMap<>();
        initializeColorsList();
    }

    private void initializeColorsList() {
        for (int i = 30; i <37; i++) {
            colors.add(new Color(i));
        }
    }

    public Color getRandomColor() {
        Random random = new Random();
        int index = random.nextInt(colors.size());
        return colors.get(index);
    }

    @Override
    public void run() {
        try {
            while (true) {
                String request = inputStream.readUTF();
                String[] parts = request.split(" ");
                String command = parts[0];
                String key = parts.length > 1 ? parts[1] : null;
                String value = parts.length > 2 ? parts[2] : null;

                handleRequest(command, key, value);
                if (command.equals(QUIT)) break;
            }

        } catch (IOException e) {
            System.out.println("Client suddenly disconnected");
        }
    }

    private void handleRequest(String command, String key, String value) throws IOException {
        switch (command) {
            case GET -> handleGetRequest(key);
            case PUT -> handlePutRequest(key, value);
            case DELETE -> handleDeleteRequest(key);
            case KEYS -> returnAllKeys();
            case QUIT -> onQuit();
            default -> printDefaultMessage();
        }
    }

    private void handleGetRequest(String key) throws IOException {
        if (keyValueStore.containsKey(key)) {
            System.out.printf("%s[%s] Success: The value under the key \"%s\" is - %s\n%s", getRandomColor() ,LocalDateTime.now().format(TestTCPServer.timeFormatter), key, keyValueStore.get(key) , ASCII_COLOR_RESET);
            outputStream.writeUTF(String.format("%s[%s] Success: The value under the key \"%s\" is - %s\n%s", getRandomColor() ,LocalDateTime.now().format(TestTCPServer.timeFormatter), key, keyValueStore.get(key) ,ASCII_COLOR_RESET));
        } else {
            printEntryDoesNotExist(key);
        }
    }

    private void printEntryDoesNotExist(String key) throws IOException {
        outputStream.writeUTF(String.format("%s[%s] The value under the key \"%s\" does not exists.\n%s", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), key, ASCII_COLOR_RESET));
    }

    private void handlePutRequest(String key, String value) throws IOException {
        if (!keyValueStore.containsKey(key)) {
            keyValueStore.put(key, value);
            printEntryPlacedSuccessfully(key, value);
        } else {
            outputStream.writeUTF(String.format("%s[%s] Error: The entry with key \"%s\" already exists\n%s", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), key, ASCII_COLOR_RESET));
        }
    }

    private void printEntryPlacedSuccessfully(String key, String value) throws IOException {
        System.out.printf("%s[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%s", getRandomColor(),LocalDateTime.now().format(TestTCPServer.timeFormatter), key, value , ASCII_COLOR_RESET);
        outputStream.writeUTF(String.format("%s[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%s", getRandomColor() ,LocalDateTime.now().format(TestTCPServer.timeFormatter), key, value , ASCII_COLOR_RESET));
    }

    private void handleDeleteRequest(String key) throws IOException {
        if (keyValueStore.containsKey(key)) {
            keyValueStore.remove(key);
            printKeyDeletedSuccessfully(key);
        } else {
            printKeyDoesNotExist(key);
        }
    }

    private void printKeyDeletedSuccessfully(String key) throws IOException {
        System.out.printf("%s[%s] Success: The key \"%s\" deleted successfully\n%s", getRandomColor(), LocalDateTime.now().format(TestTCPServer.timeFormatter), key , ASCII_COLOR_RESET);
        outputStream.writeUTF(String.format("%s[%s] Success: The key \"%s\" deleted successfully\n%s", getRandomColor(), LocalDateTime.now().format(TestTCPServer.timeFormatter), key , ASCII_COLOR_RESET));
    }

    private void printKeyDoesNotExist(String key) throws IOException {
        System.out.printf("%s[%s] Error: The key \"%s\" does not exists in the store\n%s", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), key, ASCII_COLOR_RESET);
        outputStream.writeUTF(String.format("%s[%s] Error: The key \"%s\" does not exists in the store\n%s", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), key, ASCII_COLOR_RESET));
    }

    private void returnAllKeys() {
        String res = "";
        if (!keyValueStore.keySet().isEmpty()) {
            StringBuilder builder = new StringBuilder("Success: Keys - ");
            for (String key : keyValueStore.keySet()) {
                builder.append(key).append(", ");
            }
            res = builder.substring(0, builder.length() - 2).trim();
        }

        try {
            if (!res.isEmpty())
                outputStream.writeUTF(String.format("%s%s%s" , getRandomColor(), res, ASCII_COLOR_RESET));
            else {
                String noKeysMessage = String.format("%sSuccess: Keys - There is no keys%s" , getRandomColor() , ASCII_COLOR_RESET);
                outputStream.writeUTF(noKeysMessage);
            }
            outputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onQuit() throws IOException {
        outputStream.writeUTF(QUIT);
        outputStream.flush();
        System.out.printf("%s[%s] Success: Client %s finished its work\n%s", getRandomColor(),  LocalDateTime.now().format(TestTCPServer.timeFormatter), socket.getRemoteSocketAddress().toString() , ASCII_COLOR_RESET);
    }

    private void printDefaultMessage() throws IOException {
        System.out.printf("%s[%s] Client[%s] - Wrong format of command.\n%s" , getRandomColor(), LocalDateTime.now().format(TestTCPServer.timeFormatter), socket.getRemoteSocketAddress() , ASCII_COLOR_RESET);
        outputStream.writeUTF(String.format("%s[%s] Client[%s] - Wrong format of command.\n%s" , getRandomColor(), LocalDateTime.now().format(TestTCPServer.timeFormatter), socket.getRemoteSocketAddress() , ASCII_COLOR_RESET));
    }
}