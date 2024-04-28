import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class TestTCPClient {
    public static final String ASCII_RED = "\u001B[31m";
    public static final String ASCII_RED_RESET = "\u001B[0m";

    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;

    private static final String QUIT = "QUIT";
    private static final String KEYS = "KEYS";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String GET = "GET";

    public TestTCPClient() throws IOException {
        this.socket = new Socket("localhost" ,8081);;
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    public static void main(String[] args) throws IOException {
        TestTCPClient client = new TestTCPClient();
        var consoleInput = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            printAvailableCommands();

            var currentCommand = consoleInput.readLine();

            if(!handleRequest(currentCommand , client)) {
                System.out.printf("%s[%s] Error: Write commands in correct format (Keys should consist of only letters and numbers)!%s\n", ASCII_RED, LocalDateTime.now().format(TestTCPServer.timeFormatter), ASCII_RED_RESET);
                continue;
            }

            client.outputStream.writeUTF(currentCommand);
            var received = client.inputStream.readUTF();

            if(received.equals("QUIT")) {
                closeConnection(client.outputStream , client.inputStream , client.socket);
                break;
            }

            System.out.println(received);
            client.outputStream.flush();
        }
    }

    private static boolean handleRequest(String currentCommand , TestTCPClient client) {
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

    private static boolean handlePutRequest(String[] params) {
        if(params.length != 2) {return false;}

        for(String elem: params) {
            if(elem.length() > 10) return false;
        }

        return params[0].matches("[a-zA-Z0-9]+");
    }

    private static boolean handleDelRequest(String[] params) {
        return params.length == 1 && params[0].matches("[a-zA-Z0-9]+");
    }

    private static boolean handleGetRequest(String[] params) {
        return params.length == 1 && params[0].matches("[a-zA-Z0-9]+");
    }

    private static boolean handleKeysRequest(String[] params) {
        return params.length == 0;
    }

    private static boolean handleQuitRequest(String[] params) {
        return params.length == 0;
    }

    private static void closeConnection(DataOutputStream outputStream, DataInputStream inputStream, Socket socket) throws IOException {
        System.out.printf("[%s] Success: Client %s finished its work\n", LocalDateTime.now().format(TestTCPServer.timeFormatter), socket.getRemoteSocketAddress().toString());
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    private static void printAvailableCommands() {
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

    private void cleanUp() {
        System.out.printf("[%s] Success: Client finished its work\n",getCurrentTimeStamp() );
    }

    private String getCurrentTimeStamp() {return LocalDateTime.now().format(TestTCPServer.timeFormatter);}
}

