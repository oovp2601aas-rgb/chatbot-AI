package ui;

import controller.ChatController;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import model.ChatRequest;

/**
 * SellerPanel - Dashboard untuk 1 seller
 *
 * PERUBAHAN:
 * - Punya sellerIndex (0, 1, 2) dan sellerName ("Seller 1", dst)
 * - Header menampilkan nama seller
 * - sellerIndex diteruskan ke RequestPanel agar form submit tahu seller mana
 */
public class SellerPanel extends JPanel {

    private ChatController controller;
    private JPanel         requestsContainer;
    private Map<Integer, RequestPanel> requestPanels = new HashMap<>();

    private int    sellerIndex; // 0, 1, 2
    private String sellerName;  // "Seller 1", "Seller 2", "Seller 3"

    // Warna header per seller
    private static final Color[] SELLER_COLORS = {
        new Color(33, 150, 243),   // Seller 1 - Biru
        new Color(0,  150, 136),   // Seller 2 - Teal
        new Color(233, 30, 99)     // Seller 3 - Pink
    };

    private static final String[] SELLER_ICONS = {
        "\uD83D\uDC68\u200D\uD83C\uDF73",   // 👨‍🍳 Seller 1
        "\uD83D\uDC69\u200D\uD83C\uDF73",   // 👩‍🍳 Seller 2
        "\uD83E\uDDD1\u200D\uD83C\uDF73"    // 🧑‍🍳 Seller 3
    };

    public SellerPanel(int sellerIndex) {
        this.sellerIndex = sellerIndex;
        this.sellerName  = "Seller " + (sellerIndex + 1);
        initComponents();
    }

    /** Constructor default (backward compat) */
    public SellerPanel() {
        this(0);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // ── Header ──
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        String icon  = sellerIndex < SELLER_ICONS.length  ? SELLER_ICONS[sellerIndex]  : "\uD83D\uDC68\u200D\uD83DDCBC";
        Color  color = sellerIndex < SELLER_COLORS.length ? SELLER_COLORS[sellerIndex] : new Color(33, 150, 243);

        JLabel headerLabel = new JLabel(icon + " " + sellerName + " Dashboard");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        headerLabel.setForeground(color);
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ── Requests container ──
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

    public void setController(ChatController c) {
        this.controller = c;
    }

    public String getSellerName()  { return sellerName; }
    public int    getSellerIndex() { return sellerIndex; }

    public void addRequest(ChatRequest request) {
        // Teruskan sellerIndex ke RequestPanel
        RequestPanel rp = new RequestPanel(request, controller, sellerIndex);
        requestPanels.put(request.getRequestId(), rp);
        requestsContainer.add(rp);
        requestsContainer.revalidate();
        requestsContainer.repaint();
        scrollToBottom();
    }

    public void updateRequest(ChatRequest r) {
        RequestPanel p = requestPanels.get(r.getRequestId());
        if (p != null) p.updateRequest(r);
    }

    public void fillFormField(int requestId, int formIndex, String value) {
        RequestPanel p = requestPanels.get(requestId);
        if (p != null) p.fillForm(formIndex, value);
    }

    public RequestPanel getRequestPanel(int id) {
        return requestPanels.get(id);
    }

    public void clearAllRequests() {
        requestsContainer.removeAll();
        requestPanels.clear();
        requestsContainer.revalidate();
        requestsContainer.repaint();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar sb = ((JScrollPane) requestsContainer.getParent().getParent()).getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }
}