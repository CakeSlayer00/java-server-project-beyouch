import java.io.*;
import java.net.Socket;

public class TestTCPClient {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost" ,8081);
        DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
        DataInputStream dataIn = new DataInputStream(socket.getInputStream());
        var consoleInput = new BufferedReader(new InputStreamReader(System.in));

        while(true) {
            System.out.print("""
                Please Input Command in either of the following forms:
                \tGET <key>
                \tPUT <key> <val>
                \tDELETE <key>
                \tKEYS
                \tQUIT
                """);
            System.out.print("Enter command: ");
            dataOut.writeUTF(consoleInput.readLine());
            String s = dataIn.readUTF();
            System.out.println("Received: " + s);
        }
    }
}
