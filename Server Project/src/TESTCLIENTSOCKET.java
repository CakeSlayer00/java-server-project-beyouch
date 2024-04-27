import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TESTCLIENTSOCKET {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost" ,8081);
        PrintWriter out = new PrintWriter(socket.getOutputStream() , true);
        Scanner in = new Scanner(socket.getInputStream());
        Scanner send = new Scanner(System.in);

        while(true) {
            System.out.print("Enter command: ");
            String command = send.nextLine();
            out.println(command);
            String s = in.nextLine();
            System.out.println("Received: " + s);
        }
    }
}
