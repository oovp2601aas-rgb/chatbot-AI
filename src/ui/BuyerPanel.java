package ui;

import controller.ChatController;
import javax.swing.*;
import java.awt.*;

/**
 * BuyerPanel - Redesigned UI for buyer chat
 * Matches the left panel design from the target image
 * 
 * Features:
 * - "📱 Buyer Chat" header
 * - Color-coded message bubbles (blue, yellow, green)
 * - Circular blue send button at bottom
 */
public class BuyerPanel extends JPanel {
    private JTextField messageField;
    private CircularButton sendButton;
    private JPanel chatArea;
    private ChatController controller;

    public BuyerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // Bottom border

        JLabel headerLabel = new JLabel(" Buyer Chat"); // Mobile-like icon
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(new Color(103, 58, 183)); // Deep purple/blue
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Chat display area
        chatArea = new JPanel();
        chatArea.setLayout(new BoxLayout(chatArea, BoxLayout.Y_AXIS));
        chatArea.setBackground(Color.WHITE);
        chatArea.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Input area
        JPanel inputPanel = new JPanel(new BorderLayout(15, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        // Circular purple/blue send button
        sendButton = new CircularButton("➤", new Color(103, 58, 183), 45);

        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        // Event listeners
        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty() && controller != null) {
            controller.onBuyerMessageSent(message);
            messageField.setText("");
        }
    }

    public void setController(ChatController controller) {
        this.controller = controller;
    }

    /**
     * Display a buyer message (blue bubble)
     */
    public void displayBuyerMessage(String message) {
        Bubble bubble = new Bubble(message, Bubble.BubbleType.BUYER);
        chatArea.add(bubble);
        chatArea.revalidate();
        chatArea.repaint();
        scrollToBottom();
    }

    /**
     * Display a waiting status message (yellow bubble)
     */
    public void displayWaitingMessage(String requestLabel) {
        String message = "⏳ Waiting for seller data " + requestLabel.substring(requestLabel.lastIndexOf('-') + 1)
                + "...";
        Bubble bubble = new Bubble(message, Bubble.BubbleType.WAITING);
        chatArea.add(bubble);
        chatArea.revalidate();
        chatArea.repaint();
        scrollToBottom();
    }

    /**
     * Display a seller response (green bubble)
     */
    public void displaySellerResponse(String message) {
        Bubble bubble = new Bubble(message, Bubble.BubbleType.SELLER);
        chatArea.add(bubble);
        chatArea.revalidate();
        chatArea.repaint();
        scrollToBottom();
    }

    /**
     * Remove the last waiting message and replace with seller response
     */
    public void replaceLastWaitingWithResponse(String response) {
        // Remove last component if it's a waiting bubble
        if (chatArea.getComponentCount() > 0) {
            Component lastComponent = chatArea.getComponent(chatArea.getComponentCount() - 1);
            if (lastComponent instanceof Bubble) {
                chatArea.remove(lastComponent);
            }
        }
        displaySellerResponse(response);
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) chatArea.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }

    public void clearChat() {
        chatArea.removeAll();
        chatArea.revalidate();
        chatArea.repaint();
    }
}
