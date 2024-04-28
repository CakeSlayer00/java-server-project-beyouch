import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalDateTime;

public class TestUDPClient {
    public static final String ASCII_RED = "\u001B[31m";
    public static final String ASCII_RED_RESET = "\u001B[0m";

    private DatagramSocket clientSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private InetAddress ip;
    private int port;
    private byte[] sendData;
    private byte[] receiveData = new byte[1024];

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    public TestUDPClient(DatagramSocket clientSocket, InetAddress ip) {
        this.clientSocket = clientSocket;
        this.ip = ip;
    }

    public void sendThenReceive() {
        try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while(true) {
                printAvailableCommands();

                String messageToSend =  br.readLine();

                if(!handleRequest(messageToSend)) {
                    System.out.printf("%s[%s] Error: Write commands in correct format (Keys should consist of only letters and numbers)!%s\n", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), ASCII_RED_RESET);
                    continue;
                }

                sendData = messageToSend.getBytes();
                sendPacket = new DatagramPacket(sendData, sendData.length, ip, 1234);
                clientSocket.send(sendPacket);

                receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData() , 0 , receivePacket.getLength());

                if(receivedMessage.equals(QUIT)) {
                    clientSocket.close();
                    break;
                }

                System.out.println("Received from server: " + receivedMessage);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean handleRequest(String currentCommand) {
        if (currentCommand == null || currentCommand.isEmpty()) {
            return false;
        }

        String[] parts = currentCommand.trim().split("\\s+", 2);
        String command = parts[0];
        String[] params = parts.length > 1 ? parts[1].split("\\s+") : new String[0];

        switch(command) {
            case GET -> {return handleGetRequest(params);}
            case PUT -> {return handlePutRequest(params);}
            case DELETE -> {return handleDelRequest(params);}
            case KEYS -> {return handleKeysRequest(params);}
            case QUIT -> {return handleQuitRequest(params);}
            default -> {return false;}
        }
    }

    private boolean handlePutRequest(String[] params) {
        if(params.length != 2) {return false;}

        for(String elem: params) {
            if(elem.length() > 10) {
                System.out.printf("%s[%s] Error: Parameter length shouldn't be more than 10!%s\n", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), ASCII_RED_RESET);

                return false;
            }
        }

        return params[0].matches("[a-zA-Z0-9]+");
    }

    private boolean handleDelRequest(String[] params) {
        return params.length == 1 && params[0].matches("[a-zA-Z0-9]+");
    }

    private boolean handleGetRequest(String[] params) {
        for(String elem: params) {
            if(elem.length() > 10) {
                System.out.printf("%s[%s] Error: Parameter length shouldn't be more than 10!%s\n", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), ASCII_RED_RESET);

                return false;
            }
        }

        return params.length == 1 && params[0].matches("[a-zA-Z0-9]+");
    }

    private boolean handleKeysRequest(String[] params) {
        return params.length == 0;
    }

    private boolean handleQuitRequest(String[] params) {
        return params.length == 0;
    }

    public static void main(String[] args) throws UnknownHostException, SocketException {
        DatagramSocket datagramSocket = new DatagramSocket();
        InetAddress ip = InetAddress.getByName("localhost");
        TestUDPClient testUDPClient = new TestUDPClient(datagramSocket, ip);
        testUDPClient.sendThenReceive();
    }

    private void printAvailableCommands() {
        System.out.print("""
            Please Input Command in either of the following forms:
            \tGET <key>
            \tPUT <key> <val>
            \tDELETE <key>
            \tKEYS
            \tQUIT
            """);
        System.out.print("Enter command: ");
    }
}
