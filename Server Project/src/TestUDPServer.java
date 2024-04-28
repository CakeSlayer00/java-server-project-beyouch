import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLOutput;
import java.time.LocalDateTime;
import java.util.HashMap;

public class TestUDPServer {
    public static final String ASCII_RED = "\u001B[31m";
    public static final String ASCII_RED_RESET = "\u001B[0m";

    private int port;
    private InetAddress address;
    private DatagramPacket receivePacket;
    private DatagramPacket sendPacket;
    private DatagramSocket serverSocket;
    private byte[] sendData = new byte[256];
    private byte[] receiveData = new byte[256];

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";
    private HashMap<String, String> keyValueStore;

    public TestUDPServer(DatagramSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.keyValueStore = new HashMap<>();
    }

    public void receiveThenSend() {
        while (true) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(datagramPacket);
                address = datagramPacket.getAddress();
                port = datagramPacket.getPort();

                System.out.printf("[%s] Server listening Client by address [%s]\n" , getCurrentTimeStamp() , address + ":" +port);
                String request = new String(datagramPacket.getData(), 0, datagramPacket.getLength());

                String[] parts = request.split(" ");
                String command = parts[0];
                String key = parts.length > 1 ? parts[1] : null;
                String value = parts.length > 2 ? parts[2] : null;

                handleRequest(command, key, value);
                if (command.equals(QUIT)) break;

                //System.out.println("Message: " + message + ", Client: " + datagramSocket.getLocalAddress() + ":" + datagramSocket.getLocalPort());

                //datagramPacket = new DatagramPacket(buffer , buffer.length, iNetAddress, port);
                //serverSocket.send(datagramPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
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
            System.out.printf("[%s] Success: The value under the key \"%s\" is - %s\n%n", getCurrentTimeStamp(), key, keyValueStore.get(key));

            sendData = String.format("[%s] Success: The value under the key \"%s\" is - %s\n", getCurrentTimeStamp(), key, keyValueStore.get(key)).getBytes();

            sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            serverSocket.send(sendPacket);
        } else {
            printEntryDoesNotExist(key);
        }
    }

    private void printEntryDoesNotExist(String key) throws IOException {
        sendData = String.format("%s[%s] The value under the key \"%s\" does not exists.\n%s", ASCII_RED, getCurrentTimeStamp(), key, ASCII_RED_RESET).getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);
    }

    private static String getCurrentTimeStamp() {
        return LocalDateTime.now().format(TestTCPServer.timeFormatter);
    }

    private void handlePutRequest(String key, String value) throws IOException {
        if (!keyValueStore.containsKey(key)) {
            keyValueStore.put(key, value);
            printEntryPlacedSuccessfully(key, value);
        } else {
            sendData = String.format("%s[%s] Error: The entry with key \"%s\" already exists\n%s", ASCII_RED, getCurrentTimeStamp(), key, ASCII_RED_RESET).getBytes();

            sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            serverSocket.send(sendPacket);

        }
    }

    private void printEntryPlacedSuccessfully(String key, String value) throws IOException {
        System.out.printf("[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%n", getCurrentTimeStamp(), key, value);
        //outputStream.writeUTF(String.format("[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%n", getCurrentTimeStamp(), key, value));
        sendData = String.format("[%s] Success: The entry with key \"%s\" and value \"%s\" placed successfully\n%n", getCurrentTimeStamp(), key, value).getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);
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
        System.out.printf("[%s] Success: The key \"%s\" deleted successfully\n", getCurrentTimeStamp(), key);
        //outputStream.writeUTF(String.format("[%s] Success: The key \"%s\" deleted successfully\n", getCurrentTimeStamp(), key));
        sendData = String.format("[%s] Success: The key \"%s\" deleted successfully\n", getCurrentTimeStamp(), key).getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);
    }

    private void printKeyDoesNotExist(String key) throws IOException {
        System.out.printf("%s[%s] Error: The key \"%s\" does not exists in the store\n%s", ASCII_RED, getCurrentTimeStamp(), key, ASCII_RED_RESET);
        //outputStream.writeUTF();

        sendData = String.format("%s[%s] Error: The key \"%s\" does not exists in the store\n%s", ASCII_RED, getCurrentTimeStamp(), key, ASCII_RED_RESET).getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);
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
            if (!res.isEmpty()) {
                sendData = res.getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                serverSocket.send(sendPacket);
            }
            else {
                String noKeysMessage = "Success: Keys - There is no keys";
                //outputStream.writeUTF(noKeysMessage);
                sendData = noKeysMessage.getBytes();

                sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                serverSocket.send(sendPacket);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void onQuit() throws IOException {
        //outputStream.writeUTF(QUIT);
        //outputStream.flush();
        sendData = QUIT.getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);

        System.out.printf("[%s] Success: Client %s finished its work\n", getCurrentTimeStamp(), address + ":" + port);
    }

    private void printDefaultMessage() throws IOException {
        System.out.printf("\u001B[31m" + "[%s] Client[%s] - Wrong format of command.\n" + "\u001B[0m", getCurrentTimeStamp(), address + ":" + port);
        //outputStream.writeUTF(String.format("\u001B[31m" + "[%s] Wrong format of command.\n" + "\u001B[0m", getCurrentTimeStamp()));

        sendData = String.format("\u001B[31m" + "[%s] Wrong format of command.\n" + "\u001B[0m", getCurrentTimeStamp()).getBytes();

        sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
        serverSocket.send(sendPacket);
    }

    public static void main(String[] args) throws SocketException {
        DatagramSocket datagramSocket = new DatagramSocket(1234);
        TestUDPServer testUDPServer = new TestUDPServer(datagramSocket);
        testUDPServer.receiveThenSend();
    }
}
