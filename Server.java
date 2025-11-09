import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    
    private static ArrayList<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Chat Server is running and waiting for clients...");

            while (true) {
                Socket socket = serverSocket.accept(); 
                System.out.println("New client connected: " + socket.getRemoteSocketAddress());

                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                clientWriters.add(writer); 
                ClientHandler clientThread = new ClientHandler(socket, writer);
                new Thread(clientThread).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   
    public static synchronized void broadcastMessage(String message, PrintWriter sender) {
        for (PrintWriter writer : clientWriters) {
            if (writer != sender) {
                writer.println(message);
            }
        }
    }
    
    
    public static synchronized void removeClient(PrintWriter writer) {
        clientWriters.remove(writer);
    }
}