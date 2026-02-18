package ui;

import controller.ChatController;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import model.ChatRequest;

/**
 * SellerPanel - Redesigned as multi-session dashboard
 * Matches the right panel design from the target image
 * 
 * Features:
 * - "💼 Seller Dashboard" header
 * - Scrollable list of request panels (REQ-1, REQ-2, REQ-3...)
 * - Each request has 3 forms with individual green submit buttons
 * - AI suggestions per field
 */
public class SellerPanel extends JPanel {
    private ChatController controller;
    private JPanel requestsContainer;
    private Map<Integer, RequestPanel> requestPanels; // requestId -> RequestPanel

    public SellerPanel() {
        this.requestPanels = new HashMap<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        // MENGGUNAKAN EMOJI SELLER (OFFICE WORKER)
        JLabel headerLabel = new JLabel("\uD83D\uDC68\u200D\uD83D\uDCBC Seller Dashboard"); 
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18)); // Font khusus emoji
        headerLabel.setForeground(new Color(33, 150, 243)); // Warna biru cerah untuk Seller
        headerPanel.add(headerLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Requests container (scrollable)
        requestsContainer = new JPanel();
        requestsContainer.setLayout(new BoxLayout(requestsContainer, BoxLayout.Y_AXIS));
        requestsContainer.setBackground(Color.WHITE);
        requestsContainer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JScrollPane scrollPane = new JScrollPane(requestsContainer);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setController(ChatController controller) {
        this.controller = controller;
    }

    /**
     * Add a new request to the dashboard
     */
    public void addRequest(ChatRequest request) {
        RequestPanel requestPanel = new RequestPanel(request, controller);
        requestPanels.put(request.getRequestId(), requestPanel);

        requestsContainer.add(requestPanel);
        requestsContainer.revalidate();
        requestsContainer.repaint();

        scrollToBottom();
    }

    /**
     * Update an existing request panel
     */
    public void updateRequest(ChatRequest request) {
        RequestPanel panel = requestPanels.get(request.getRequestId());
        if (panel != null) {
            panel.updateRequest(request);
        }
    }

    /**
     * Fill a specific form field with AI suggestion
     */
    public void fillFormField(int requestId, int formIndex, String value) {
        RequestPanel panel = requestPanels.get(requestId);
        if (panel != null) {
            panel.fillForm(formIndex, value);
        }
    }

    /**
     * Get a request panel by ID
     */
    public RequestPanel getRequestPanel(int requestId) {
        return requestPanels.get(requestId);
    }

    /**
     * Clear all requests from dashboard
     */
    public void clearAllRequests() {
        requestsContainer.removeAll();
        requestPanels.clear();
        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = ((JScrollPane) requestsContainer.getParent().getParent()).getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
