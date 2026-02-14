package ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

/**
 * Bubble - UI component for displaying chat messages
 * Modernized with rounded corners, padding, and proper alignment.
 */
public class Bubble extends JPanel {

    public enum BubbleType {
        BUYER,
        WAITING,
        SELLER
    }

    private String message;
    private BubbleType type;

    // Colors
    private static final Color COLOR_BUYER = new Color(227, 242, 253); // Light Blue
    private static final Color COLOR_WAITING = new Color(255, 253, 231); // Light Yellow
    private static final Color COLOR_SELLER = new Color(232, 245, 233); // Light Green
    private static final Color COLOR_BORDER = new Color(230, 230, 230); // Subtle border

    private int requestId = -1;
    private int formIndex = -1;

    public String getText() {
        return message;
    }

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

    public BubbleType getType() {
        return type;
    }

    public Bubble(String message, BubbleType type) {
        this.message = message;
        this.type = type;
        initComponents();
    }

    private void initComponents() {
        // 1. Wrapper Layout (This Panel)
        // Uses FlowLayout to align LEFT or RIGHT
        setOpaque(false);
        if (type == BubbleType.BUYER) {
            setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        } else {
            setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5));
        }

        // 2. Inner Bubble Panel
        // Holds the text and paints the rounded background
        JPanel bubbleContent = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Determine color
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
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bgColor);
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20); // 20px radius
                g2.setColor(COLOR_BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        bubbleContent.setOpaque(false); // We paint background manually
        bubbleContent.setLayout(new BorderLayout());
        // Padding inside the bubble: EmptyBorder(10, 15, 10, 15)
        bubbleContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // 3. Message Label
        // Uses HTML for wrapping. Max width approx 400px (controlled by HTML body
        // width)
        // Use styled paragraph to ensure proper wrapping
        String htmlMessage = "<html><body style='width: 350px; text-align: left;'>"
                + message.replace("\n", "<br>")
                + "</body></html>";

        JLabel messageLabel = new JLabel(htmlMessage);
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageLabel.setForeground(Color.BLACK);

        // If message is short, let JLabel auto-size (don't force 350px if not needed)
        // The HTML 'width' usually forces preferred size.
        // A trick for dynamic width: use basic HTML, but override preferred size?
        // Actually, 'width' in body forces it.
        // Let's check text length.
        if (message.length() < 50 && !message.contains("\n")) {
            // Short message: remove fixed width to let it shrink
            htmlMessage = "<html><body style='text-align: left;'>"
                    + message
                    + "</body></html>";
            messageLabel.setText(htmlMessage);
        }

        bubbleContent.add(messageLabel, BorderLayout.CENTER);

        add(bubbleContent);
    }

    /**
     * Custom RoundedBorder (Requested Implementation)
     * Note: I used paintComponent in JPanel above for better background filling,
     * but here is the class as requested if we wanted to use it strictly as a
     * Border.
     */
    public static class RoundedBorder extends AbstractBorder {
        private Color color;
        private int radius;

        public RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
    }
}
