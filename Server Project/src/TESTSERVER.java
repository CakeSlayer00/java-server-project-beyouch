import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class TESTSERVER {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8081);

        while(true) {
            Socket socket = serverSocket.accept();
            Thread thread = new Thread(new ClientHandler(socket));
            thread.start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            Scanner in = new Scanner(socket.getInputStream());
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            Scanner send = new Scanner(System.in);

            while(in.hasNext()) {
                System.out.println("Received: " + in.nextLine());
                System.out.print("Enter: ");
                String s = send.nextLine();
                out.println(s);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
