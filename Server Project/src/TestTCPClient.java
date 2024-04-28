import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;

public class TestTCPClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost" ,8081);
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        var consoleInput = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            printAvailableCommands();
            outputStream.writeUTF(consoleInput.readLine());
            var received = inputStream.readUTF();

            if(received.equals("QUIT")) {
                closeConnection(outputStream , inputStream , socket);
                break;
            }

            System.out.println(received);
            outputStream.flush();
        }
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
}
