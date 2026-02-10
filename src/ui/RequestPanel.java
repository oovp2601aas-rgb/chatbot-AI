package ui;

import controller.ChatController;
import model.ChatRequest;
import javax.swing.*;
import java.awt.*;

/**
 * RequestPanel - Individual request section for seller dashboard
 * Each request (REQ-1, REQ-2, REQ-3) is displayed as a separate RequestPanel
 * 
 * Features:
 * - Request label (e.g., "REQ-1")
 * - Buyer message display
 * - 3 form fields (Form 1, Form 2, Form 3)
 * - Green circular submit button per field
 * - AI suggest functionality per field
 */
public class RequestPanel extends JPanel {
    private ChatRequest request;
    private ChatController controller;

    private JLabel requestLabel;
    private JLabel buyerMessageLabel;
    private JTextField form1Field;
    private JTextField form2Field;
    private JTextField form3Field;

    public RequestPanel(ChatRequest request, ChatController controller) {
        this.request = request;
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        // Set preferred and minimum size to ensure visibility
        setPreferredSize(new Dimension(450, 220));
        setMinimumSize(new Dimension(400, 220));

        // Request label (e.g., "REQ-1")
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        requestLabel = new JLabel(request.getRequestLabel());
        requestLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        requestLabel.setForeground(Color.BLACK);
        labelPanel.add(requestLabel);
        add(labelPanel);

        add(Box.createVerticalStrut(5));

        // Buyer message display
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        buyerMessageLabel = new JLabel(request.getBuyerMessage());
        buyerMessageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        buyerMessageLabel.setForeground(new Color(100, 100, 100));
        messagePanel.add(buyerMessageLabel);
        add(messagePanel);

        add(Box.createVerticalStrut(10));

        // Form 1
        add(createFormRow("Form 1", 1));
        add(Box.createVerticalStrut(8));

        // Form 2
        add(createFormRow("Form 2", 2));
        add(Box.createVerticalStrut(8));

        // Form 3
        add(createFormRow("Form 3", 3));

        add(Box.createVerticalStrut(5));
    }

    /**
     * Create a form row with label, text field, and green submit button
     */
    private JPanel createFormRow(String label, int formIndex) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setBackground(Color.WHITE);
        rowPanel.setPreferredSize(new Dimension(420, 36));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        // Form label
        JLabel formLabel = new JLabel(label);
        formLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formLabel.setPreferredSize(new Dimension(60, 32));
        formLabel.setForeground(new Color(150, 150, 150));
        rowPanel.add(formLabel, BorderLayout.WEST);

        // Text field
        JTextField textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBackground(new Color(245, 255, 245)); // Very light green
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 240, 230), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        // Store reference to field
        switch (formIndex) {
            case 1:
                form1Field = textField;
                if (request.getProductExplanation() != null) {
                    form1Field.setText(request.getProductExplanation());
                }
                break;
            case 2:
                form2Field = textField;
                if (request.getPriceEstimation() != null) {
                    form2Field.setText(request.getPriceEstimation());
                }
                break;
            case 3:
                form3Field = textField;
                if (request.getStockAvailability() != null) {
                    form3Field.setText(request.getStockAvailability());
                }
                break;
        }
        rowPanel.add(textField, BorderLayout.CENTER);

        // Buttons container
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // AI Suggest button (Purple/Blue) - Smaller
        CircularButton aiButton = new CircularButton("✨", new Color(103, 58, 183), 32);
        aiButton.setToolTipText("Get AI Suggestion");
        aiButton.addActionListener(e -> controller.onAISuggestRequested(request.getRequestId(), formIndex));
        buttonsPanel.add(aiButton);

        // Green circular submit button with arrow icon
        CircularButton submitButton = new CircularButton("➤", new Color(0, 150, 136), 32);
        submitButton.addActionListener(e -> handleFormSubmit(formIndex));
        buttonsPanel.add(submitButton);

        rowPanel.add(buttonsPanel, BorderLayout.EAST);

        return rowPanel;
    }

    /**
     * Handle form submission for individual field
     */
    private void handleFormSubmit(int formIndex) {
        String value = "";

        switch (formIndex) {
            case 1:
                value = form1Field.getText().trim();
                if (!value.isEmpty()) {
                    controller.onSellerFormSubmit(request.getRequestId(), formIndex, value);
                }
                break;
            case 2:
                value = form2Field.getText().trim();
                if (!value.isEmpty()) {
                    controller.onSellerFormSubmit(request.getRequestId(), formIndex, value);
                }
                break;
            case 3:
                value = form3Field.getText().trim();
                if (!value.isEmpty()) {
                    controller.onSellerFormSubmit(request.getRequestId(), formIndex, value);
                }
                break;
        }
    }

    /**
     * Auto-fill a specific form field with AI suggestion
     */
    public void fillForm(int formIndex, String value) {
        switch (formIndex) {
            case 1:
                form1Field.setText(value);
                break;
            case 2:
                form2Field.setText(value);
                break;
            case 3:
                form3Field.setText(value);
                break;
        }
    }

    /**
     * Get the request associated with this panel
     */
    public ChatRequest getRequest() {
        return request;
    }

    /**
     * Update the request data
     */
    public void updateRequest(ChatRequest updatedRequest) {
        this.request = updatedRequest;

        // Update fields if they have new data
        if (updatedRequest.getProductExplanation() != null && form1Field.getText().isEmpty()) {
            form1Field.setText(updatedRequest.getProductExplanation());
        }
        if (updatedRequest.getPriceEstimation() != null && form2Field.getText().isEmpty()) {
            form2Field.setText(updatedRequest.getPriceEstimation());
        }
        if (updatedRequest.getStockAvailability() != null && form3Field.getText().isEmpty()) {
            form3Field.setText(updatedRequest.getStockAvailability());
        }
    }
}
