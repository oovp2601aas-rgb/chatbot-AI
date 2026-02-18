package main;

import controller.ChatController;
import java.awt.*;
import javax.swing.*;
import ui.BuyerPanel;
import ui.SellerPanel;

/**
 * Main Application - Entry point for the multi-session chat application
 * 
 * ARCHITECTURE:
 * - MVC Pattern: Model (ChatRequest), View (BuyerPanel, SellerPanel),
 * Controller (ChatController)
 * - Mediator Pattern: ChatController mediates between buyer and seller panels
 * - Service Layer: SellerAIService provides AI functionality
 * 
 * NEW FEATURES:
 * - Multi-session support (REQ-1, REQ-2, REQ-3...)
 * - Color-coded message bubbles (blue, yellow, green)
 * - Circular send buttons
 * - AI suggestions per form field
 */
public class ChatApplication extends JFrame {
    private ChatController controller;
    private BuyerPanel buyerPanel;
    private SellerPanel sellerPanel;

    public ChatApplication() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Multi-Session Chat System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container with background and padding
        JPanel mainContainer = new JPanel(new GridLayout(1, 2, 20, 20));
        mainContainer.setBackground(new Color(225, 235, 245)); // Light blue-gray background
        mainContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Initialize controller (Mediator)
        controller = new ChatController();

        // Initialize UI panels
        buyerPanel = new BuyerPanel();
        sellerPanel = new SellerPanel();

        // Connect panels to controller
        controller.setBuyerPanel(buyerPanel);
        controller.setSellerPanel(sellerPanel);

        // Add panels to main container
        mainContainer.add(buyerPanel);
        mainContainer.add(sellerPanel);

        add(mainContainer);

        // Menu bar
        createMenuBar();

        // Window settings
        setSize(1100, 750);
        setLocationRelativeTo(null);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenuItem clearItem = new JMenuItem("Clear All Chats");
        clearItem.addActionListener(e -> controller.clearAllChats());

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(clearItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showAboutDialog() {
        String message = "Multi-Session Chat System\n\n" +
                "Features:\n" +
                "• Multi-session buyer-seller communication\n" +
                "• AI-powered seller assistant (per field)\n" +
                "• Color-coded message bubbles\n" +
                "• Individual form field submission\n\n" +
                "Architecture:\n" +
                "• MVC Pattern\n" +
                "• Mediator Pattern (ChatController)\n" +
                "• Service Layer (SellerAIService)\n\n" +
                "How to use:\n" +
                "1. Buyer sends messages\n" +
                "2. Each message creates a new request (REQ-1, REQ-2...)\n" +
                "3. AI auto-fills seller form fields\n" +
                "4. Seller can edit and submit each field individually\n" +
                "5. Green submit button per field\n\n" +
                "Built with Java Swing";

        JOptionPane.showMessageDialog(this, message, "About", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show application
        SwingUtilities.invokeLater(() -> {
            ChatApplication app = new ChatApplication();
            app.setVisible(true);

            System.out.println("==============================================");
            System.out.println("Multi-Session Chat System Started");
            System.out.println("==============================================");
            System.out.println("AI Service: SellerAIService (Mock Rule-Based)");
            System.out.println("Architecture: MVC + Mediator Pattern");
            System.out.println("Features: Multi-session, AI per field");
            System.out.println("==============================================\n");
        });
    }
}