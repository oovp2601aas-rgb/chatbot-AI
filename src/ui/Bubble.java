package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.AbstractBorder;

public class Bubble extends JPanel {

    public enum BubbleType {
        BUYER,
        WAITING,
        SELLER
    }

    private String message;
    private BubbleType type;

    private int requestId = -1;
    private int formIndex = -1;

    private controller.ChatController controller; // new

    private static final Color COLOR_BUYER = new Color(227, 242, 253);
    private static final Color COLOR_WAITING = new Color(255, 253, 231);
    private static final Color COLOR_SELLER = new Color(232, 245, 233);
    private static final Color COLOR_BORDER = new Color(230, 230, 230);

    public Bubble(String message, BubbleType type) {
        this.message = message;
        this.type = type;
        initComponents();
    }

    // new
    public void setController(controller.ChatController controller){
        this.controller = controller;
    }

    private void initComponents() {
        setOpaque(false);

        if (type == BubbleType.BUYER) {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        } else {
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        }

        JPanel bubbleContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Color bgColor;

                switch (type) {
                    case BUYER:
                        bgColor = COLOR_BUYER;
                        break;
                    case WAITING:
                        bgColor = COLOR_WAITING;
                        break;
                    default:
                        bgColor = COLOR_SELLER;
                        break;
                }

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        bubbleContent.setOpaque(false);
        bubbleContent.setLayout(new BorderLayout());
        bubbleContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String htmlMessage = "<html><body style='width: 350px;'>"
                + message.replace("\n", "<br>")
                + "</body></html>";

        JLabel messageLabel = new JLabel(htmlMessage);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);

        bubbleContent.add(messageLabel, BorderLayout.CENTER);

        // SELLER BUTTON
        if (type == BubbleType.SELLER) {

            JButton pilihButton = new JButton("Choose");
            pilihButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
            pilihButton.setForeground(new Color(0, 150, 136));
            pilihButton.setBackground(Color.WHITE);
            pilihButton.setFocusPainted(false);
            pilihButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

            pilihButton.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(new Color(0, 150, 136), 15),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            final boolean[] selected = {false};

            pilihButton.addActionListener(e -> {
                if (!selected[0]) {

                    // Background putih
                    pilihButton.setBackground(Color.WHITE);
                    pilihButton.setOpaque(true);
                    pilihButton.setContentAreaFilled(true);

                    // Text hijau tua
                    Color darkGreen = new Color(27, 94, 32);
                    pilihButton.setForeground(darkGreen);
                    // Menggunakan emoji Centang Hijau (White Heavy Check Mark)
                    pilihButton.setText("\u2705 Chosen");
                    pilihButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14)); // Agar emoji tidak kotak

                    // Border hijau tua tebal
                    pilihButton.setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(darkGreen, 15),
                            BorderFactory.createEmptyBorder(6, 18, 6, 18)
                    ));

                    selected[0] = true;

                    // new
                    if (controller != null){
                        controller.onBuyerChoose(requestId, formIndex, message);
                    }
                }
            });


            JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 5));
            bottomPanel.setOpaque(false);
            bottomPanel.add(pilihButton);

            bubbleContent.add(bottomPanel, BorderLayout.SOUTH);
        }

        add(bubbleContent);
    }

    // =========================
    // GETTER & SETTER (PENTING)
    // =========================

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setFormIndex(int formIndex) {
        this.formIndex = formIndex;
    }

    public int getFormIndex() {
        return formIndex;
    }

    public String getText() {
        return message;
    }

    public BubbleType getType() {
        return type;
    }

    // =========================
    // ROUNDED BORDER
    // =========================
    public static class RoundedBorder extends AbstractBorder {

        private Color color;
        private int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g,
                                int x, int y, int width, int height) {

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 8, 8, 8);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}