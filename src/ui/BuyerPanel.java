package ui;

import controller.ChatController;
import java.awt.*;
import javax.swing.*;

/**
 * BuyerPanel - Buyer Chat (left panel)
 *
 * PERUBAHAN:
 * - replaceSpecificWaitingBubble selalu meng-set controller pada seller bubble
 *   agar tombol [ - ] qty [ + ] dan [ Choose ] bisa berkomunikasi ke controller
 */
public class BuyerPanel extends JPanel {
    private JTextField     messageField;
    private JTextField     addressField;   // ← alamat pengiriman buyer
    private CircularButton sendButton;
    private JPanel         chatArea;
    private ChatController controller;

    public BuyerPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        setBackground(Color.WHITE);

        // ── Header ──
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        JLabel headerLabel = new JLabel("\uD83D\uDECD Buyer Chat");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
        headerLabel.setForeground(new Color(103, 58, 183));
        headerPanel.add(headerLabel);
        add(headerPanel, BorderLayout.NORTH);

        // ── Chat area ──
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

        // ── Bottom area (address + message input) ──
        JPanel bottomArea = new JPanel(new BorderLayout(0, 0));
        bottomArea.setBackground(Color.WHITE);
        bottomArea.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        // -- Address row --
        JPanel addressPanel = new JPanel(new BorderLayout(8, 0));
        addressPanel.setBackground(Color.WHITE);
        addressPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 20));

        JLabel addressIcon = new JLabel("\uD83D\uDCCD Alamat:");
        addressIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 13));
        addressIcon.setForeground(new Color(103, 58, 183));
        addressIcon.setPreferredSize(new Dimension(80, 32));

        addressField = new JTextField();
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        addressField.setToolTipText("Masukkan alamat pengiriman...");
        addressField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 180, 240), 1, true),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));

        addressPanel.add(addressIcon,   BorderLayout.WEST);
        addressPanel.add(addressField,  BorderLayout.CENTER);

        // -- Message row --
        JPanel inputPanel = new JPanel(new BorderLayout(15, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(8, 20, 15, 20));

        messageField = new JTextField();
        messageField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        sendButton = new CircularButton("➤", new Color(103, 58, 183), 45);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton,   BorderLayout.EAST);

        bottomArea.add(addressPanel, BorderLayout.NORTH);
        bottomArea.add(inputPanel,   BorderLayout.CENTER);

        add(bottomArea, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());
        messageField.addActionListener(e -> sendMessage());
    }

    private void sendMessage() {
        String msg = messageField.getText().trim();
        if (!msg.isEmpty() && controller != null) {
            controller.onBuyerMessageSent(msg);
            messageField.setText("");
        }
    }

    public void setController(ChatController controller) {
        this.controller = controller;
    }

    /** Ambil alamat yang diisi buyer, return string kosong jika belum diisi */
    public String getAddress() {
        String addr = addressField.getText().trim();
        return addr.isEmpty() ? "" : addr;
    }

    // ──────────────────────────────────────────────────────────
    //  Display methods
    // ──────────────────────────────────────────────────────────

    public void displayBuyerMessage(String message) {
        Bubble b = new Bubble(message, Bubble.BubbleType.BUYER);
        chatArea.add(b);
        refresh();
    }

    public void displayWaitingMessage(String requestLabel, int requestId, int formIndex) {
        String msg = "⏳ Waiting for seller data "
                + requestLabel.substring(requestLabel.lastIndexOf('-') + 1) + "...";
        Bubble b = new Bubble(msg, Bubble.BubbleType.WAITING);
        b.setRequestId(requestId);
        b.setFormIndex(formIndex);
        chatArea.add(b);
        refresh();
    }

    public void displaySellerResponse(String message) {
        Bubble b = new Bubble(message, Bubble.BubbleType.SELLER);
        b.setController(controller);   // ← PENTING
        chatArea.add(b);
        refresh();
    }

    /**
     * Ganti waiting bubble dengan seller bubble yang sudah punya
     * stepper [ - ] qty [ + ] dan tombol [ Choose ]
     */
    public void replaceSpecificWaitingBubble(int requestId, int formIndex, String response) {
        for (int i = 0; i < chatArea.getComponentCount(); i++) {
            Component comp = chatArea.getComponent(i);
            if (!(comp instanceof Bubble)) continue;

            Bubble b = (Bubble) comp;
            if (b.getRequestId() != requestId || b.getFormIndex() != formIndex) continue;

            // Kalau sudah seller bubble dengan isi sama, skip (hindari flicker)
            if (b.getText().equals(response) && b.getType() == Bubble.BubbleType.SELLER) return;

            chatArea.remove(i);

            Bubble seller = new Bubble(response, Bubble.BubbleType.SELLER);
            seller.setController(controller);  // ← PENTING: stepper & Choose butuh controller
            seller.setRequestId(requestId);
            seller.setFormIndex(formIndex);

            chatArea.add(seller, i);
            refresh();
            return;
        }
    }

    /**
     * Tampilkan ringkasan pesanan (lavender bubble + tombol Confirm)
     * Selalu replace panel SUMMARY_PANEL yang lama agar tidak numpuk.
     */
    public void displayBuyerSummary(String message) {
        // Hapus panel summary lama
        for (int i = chatArea.getComponentCount() - 1; i >= 0; i--) {
            Component c = chatArea.getComponent(i);
            if (c instanceof JPanel && "SUMMARY_PANEL".equals(((JPanel) c).getName())) {
                chatArea.remove(i);
            }
        }

        JPanel summaryPanel = new JPanel();
        summaryPanel.setName("SUMMARY_PANEL");
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setOpaque(false);
        summaryPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea area = new JTextArea(message);
        area.setWrapStyleWord(true);
        area.setLineWrap(true);
        area.setEditable(false);
        area.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        area.setBackground(new Color(197, 202, 233));
        area.setForeground(new Color(40, 53, 147));
        area.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
        area.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color lavBlue = new Color(121, 134, 203);
        JButton confirmBtn = new JButton("Confirm Purchase");
        confirmBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setBackground(lavBlue);
        confirmBtn.setOpaque(true);
        confirmBtn.setContentAreaFilled(true);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmBtn.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        confirmBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { confirmBtn.setBackground(new Color(94, 108, 194)); }
            public void mouseExited (java.awt.event.MouseEvent e) { confirmBtn.setBackground(lavBlue); }
        });
        confirmBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Purchase Confirmed!", "Success",
                        JOptionPane.INFORMATION_MESSAGE));

        summaryPanel.add(area);
        summaryPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        summaryPanel.add(confirmBtn);

        chatArea.add(summaryPanel);
        refresh();
    }

    public void showQuickOptions(String[] options) {
        JPanel optPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        optPanel.setOpaque(false);
        optPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        for (String opt : options) {
            JButton btn = createStyledButton(opt);
            btn.addActionListener(e -> {
                if (controller != null) controller.onBuyerMessageSent(opt);
                chatArea.remove(optPanel);
                refresh();
            });
            optPanel.add(btn);
        }
        chatArea.add(optPanel);
        refresh();
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btn.setForeground(new Color(60, 60, 60));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                new Bubble.RoundedBorder(new Color(200, 200, 200), 15),
                BorderFactory.createEmptyBorder(5, 12, 5, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(new Bubble.RoundedBorder(new Color(103, 58, 183), 15), BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                btn.setForeground(new Color(103, 58, 183));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBorder(BorderFactory.createCompoundBorder(new Bubble.RoundedBorder(new Color(200, 200, 200), 15), BorderFactory.createEmptyBorder(5, 12, 5, 12)));
                btn.setForeground(new Color(60, 60, 60));
            }
        });
        return btn;
    }

    // ──────────────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────────────
    private void refresh() {
        chatArea.revalidate();
        chatArea.repaint();
        scrollToBottom();
    }

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar sb = ((JScrollPane) chatArea.getParent().getParent()).getVerticalScrollBar();
            sb.setValue(sb.getMaximum());
        });
    }

    public void clearChat() {
        chatArea.removeAll();
        addressField.setText("");
        refresh();
    }

    /** deprecated – masih ada untuk kompatibilitas */
    public void replaceLastWaitingWithResponse(String response) { }
}