import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatClientGUI {

    private JFrame frame;
    private JList<ChatMessage> messageList;
    private DefaultListModel<ChatMessage> listModel;
    
    private JTextField textField;
    private JButton sendButton;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String username;

    private Color chatBg = Color.WHITE;
    private Color inputPanelBg = new Color(245, 245, 245);
    private Color sendButtonBg = new Color(0, 122, 255);
    private Color sendButtonFg = Color.WHITE;

    public ChatClientGUI() {
        frame = new JFrame("Chat Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);

        listModel = new DefaultListModel<>();
        messageList = new JList<>(listModel);
        messageList.setCellRenderer(new ChatBubbleRenderer()); 
        messageList.setBackground(chatBg);
        messageList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        messageList.setFixedCellHeight(-1);
        
        messageList.setSelectionBackground(chatBg);
        messageList.setSelectionForeground(Color.BLACK);

        JScrollPane scrollPane = new JScrollPane(messageList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(inputPanelBg);
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            new EmptyBorder(5, 8, 5, 8)
        ));
        bottomPanel.add(textField, BorderLayout.CENTER);

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setBackground(sendButtonBg);
        sendButton.setForeground(sendButtonFg);
        sendButton.setOpaque(true);
        sendButton.setBorderPainted(false);
        sendButton.setFocusPainted(false);
        sendButton.setPreferredSize(new Dimension(80, 0));
        
        bottomPanel.add(sendButton, BorderLayout.EAST);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        textField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String messageText = textField.getText();
        if (!messageText.trim().isEmpty()) {
            
            String formattedMessage = "You: " + messageText;
            ChatMessage message = new ChatMessage(formattedMessage, true); 
            addMessageToList(message);

            writer.println(messageText);
            textField.setText("");
            
            if (messageText.equalsIgnoreCase("/exit")) {
                try {
                    socket.close(); 
                } catch (Exception e) { e.printStackTrace(); }
                System.exit(0); 
            }
        }
    }
    
    private synchronized void addMessageToList(ChatMessage message) {
        listModel.addElement(message);
        
        SwingUtilities.invokeLater(() -> {
            int lastIndex = listModel.getSize() - 1;
            if (lastIndex >= 0) {
                messageList.ensureIndexIsVisible(lastIndex);
            }
        });
    }


    private void start() {
        try {
            socket = new Socket("localhost", 1234);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            String serverPrompt = reader.readLine(); 
            this.username = JOptionPane.showInputDialog(
                frame, serverPrompt, "Username", JOptionPane.PLAIN_MESSAGE
            );
            
            if (this.username == null || this.username.trim().isEmpty()) {
                this.username = "Guest"; 
            }

            writer.println(this.username); 
            frame.setTitle("Chat Client - " + this.username);
            frame.setVisible(true); 

            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        ChatMessage message = new ChatMessage(serverMessage, false); 
                        addMessageToList(message);
                    }
                } catch (Exception e) {
                    ChatMessage errorMsg = new ChatMessage("Connection to server lost.", false);
                    addMessageToList(errorMsg);
                }
            });
            listenerThread.start();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, 
                "Could not connect to the server at localhost:1234.", 
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            ChatClientGUI client = new ChatClientGUI();
            client.start();
        });
    }
}