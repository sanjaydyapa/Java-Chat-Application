import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ChatBubbleRenderer extends JPanel implements ListCellRenderer<ChatMessage> {

    private JLabel label = new JLabel();
    private JPanel bubblePanel = new JPanel(new BorderLayout());

 
    private Color senderBubble = new Color(0, 122, 255);
    private Color senderText = Color.WHITE;
    private Color receiverBubble = new Color(229, 229, 234); 
    private Color receiverText = Color.BLACK;

    public ChatBubbleRenderer() {
        super(new BorderLayout());
        
        bubblePanel.add(label, BorderLayout.CENTER);
       
        bubblePanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        bubblePanel.setOpaque(true);
        label.setOpaque(true);
        
        
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ChatMessage> list,
                                                  ChatMessage message,
                                                  int index,
                                                  boolean isSelected,
                                                  boolean cellHasFocus) {
        
        
        String htmlText = "<html><body style='width: 250px;'>" + 
                          escapeHtml(message.getMessage()) + 
                          "</body></html>";
        label.setText(htmlText);
        
        
        removeAll();

        if (message.isSender()) {
       
            label.setBackground(senderBubble);
            label.setForeground(senderText);
            bubblePanel.setBackground(senderBubble);
            
            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.add(bubblePanel);
            rightPanel.setBackground(list.getBackground()); 
            rightPanel.setOpaque(true);
            
           
            add(rightPanel, BorderLayout.CENTER);

        } else {
        
            label.setBackground(receiverBubble);
            label.setForeground(receiverText);
            bubblePanel.setBackground(receiverBubble);

            
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.add(bubblePanel);
            leftPanel.setBackground(list.getBackground());
            leftPanel.setOpaque(true);
            
            
            add(leftPanel, BorderLayout.CENTER);
        }

       
        setBackground(list.getBackground());

      
        if (isSelected) {
           
        }

        return this;
    }
    

    private String escapeHtml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}