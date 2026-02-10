package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Bubble - UI component for displaying chat messages
 * Updated with new color scheme for multi-session chat
 * 
 * Colors:
 * - Blue (#B8C5E0): Buyer messages
 * - Yellow (#FFF9C4): Waiting status
 * - Green (#C8E6C9): Seller responses
 */
public class Bubble extends JPanel {

    public enum BubbleType {
        BUYER, // Blue bubble
        WAITING, // Yellow bubble
        SELLER // Green bubble
    }

    private String message;
    private BubbleType type;

    public Bubble(String message, BubbleType type) {
        this.message = message;
        this.type = type;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setOpaque(false);

        JTextArea textArea = new JTextArea(message);
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setFocusable(false);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        textArea.setOpaque(true);

        // Set colors based on bubble type
        switch (type) {
            case BUYER:
                textArea.setBackground(new Color(227, 242, 253)); // Light blue
                textArea.setForeground(Color.BLACK);
                setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5)); // Buyer on right
                break;
            case WAITING:
                textArea.setBackground(new Color(255, 253, 231)); // Light yellow
                textArea.setForeground(Color.BLACK);
                setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
                break;
            case SELLER:
                textArea.setBackground(new Color(232, 245, 233)); // Light green
                textArea.setForeground(Color.BLACK);
                setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
                break;
        }

        // Rounded corners effect (simulated with border)
        textArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(textArea.getBackground(), 5, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        textArea.setMaximumSize(new Dimension(350, Integer.MAX_VALUE));
        add(textArea);

        setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
    }
}
