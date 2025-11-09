import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    
    private Socket socket;
    private PrintWriter writer;
    private String username; 

    public ClientHandler(Socket socket, PrintWriter writer) {
        this.socket = socket;
        this.writer = writer;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream())
            );

            
            writer.println("Enter your username:"); 
            this.username = reader.readLine();
            
            if (this.username == null || this.username.trim().isEmpty()) {
                this.username = "Guest-" + socket.getPort();
            }
            
            System.out.println(this.username + " has joined.");
            
            Server.broadcastMessage(this.username + " has joined the chat!", this.writer);

            String clientMessage;
            
      
            while ((clientMessage = reader.readLine()) != null) {
                
               
                if (clientMessage.equalsIgnoreCase("/exit")) {
                    break; 
                }

                
                String broadcastMsg = this.username + ": " + clientMessage;
                System.out.println("Received from " + this.username + ": " + clientMessage);
                Server.broadcastMessage(broadcastMsg, this.writer);
            }
        } catch (Exception e) {
            System.out.println("Client " + (username != null ? username : "") + " disconnected.");
        } finally {
            
            if (writer != null) {
                Server.removeClient(writer);
            }
            
          
            if (this.username != null) {
                System.out.println(this.username + " has left.");
                Server.broadcastMessage(this.username + " has left the chat.", this.writer);
            }
            
           
            try {
                socket.close();
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
    }
}