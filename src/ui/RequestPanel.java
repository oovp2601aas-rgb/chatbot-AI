package ui;

import controller.ChatController;
import java.awt.*;
import javax.swing.*;
import model.ChatRequest;

/**
 * RequestPanel - Form per request di dashboard seller
 *
 * PERUBAHAN:
 * - Punya sellerIndex, diteruskan ke controller saat submit/AI
 */
public class RequestPanel extends JPanel {
    private ChatRequest    request;
    private ChatController controller;
    private int            sellerIndex; // seller ke berapa (0,1,2)

    private JTextArea form1Field;
    private JTextArea form2Field;
    private JTextArea form3Field;

    public RequestPanel(ChatRequest request, ChatController controller, int sellerIndex) {
        this.request     = request;
        this.controller  = controller;
        this.sellerIndex = sellerIndex;
        initComponents();
    }

    /** Backward compat */
    public RequestPanel(ChatRequest request, ChatController controller) {
        this(request, controller, 0);
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        setPreferredSize(new Dimension(450, 320));
        setMinimumSize(new Dimension(400, 320));

        // Request label
        JPanel lp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        lp.setBackground(Color.WHITE);
        lp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel rl = new JLabel(request.getRequestLabel());
        rl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lp.add(rl);
        add(lp);

        add(Box.createVerticalStrut(5));

        // Buyer message
        JPanel mp = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        mp.setBackground(Color.WHITE);
        mp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        JLabel ml = new JLabel(request.getBuyerMessage());
        ml.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        ml.setForeground(new Color(100, 100, 100));
        mp.add(ml);
        add(mp);

        add(Box.createVerticalStrut(10));

        add(createFormRow("Form 1", 1));
        add(Box.createVerticalStrut(8));
        add(createFormRow("Form 2", 2));
        add(Box.createVerticalStrut(8));
        add(createFormRow("Form 3", 3));
        add(Box.createVerticalStrut(5));
    }

    private JPanel createFormRow(String label, int formIndex) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(Color.WHITE);
        row.setPreferredSize(new Dimension(420, 80));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel fl = new JLabel(label);
        fl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fl.setPreferredSize(new Dimension(60, 32));
        fl.setForeground(new Color(150, 150, 150));
        row.add(fl, BorderLayout.WEST);

        JTextArea ta = new JTextArea();
        ta.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        ta.setBackground(new Color(245, 255, 245));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));

        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 240, 230), 1, true));
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        switch (formIndex) {
            case 1: form1Field = ta; if (request.getProductExplanation() != null) ta.setText(request.getProductExplanation()); break;
            case 2: form2Field = ta; if (request.getPriceEstimation()    != null) ta.setText(request.getPriceEstimation());    break;
            case 3: form3Field = ta; if (request.getStockAvailability()  != null) ta.setText(request.getStockAvailability());  break;
        }
        row.add(sp, BorderLayout.CENTER);

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        bp.setBackground(Color.WHITE);

        // AI button — teruskan sellerIndex
        CircularButton ai = new CircularButton("AI", new Color(103, 58, 183), 32);
        ai.setToolTipText("AI Suggestion");
        ai.addActionListener(e -> controller.onAISuggestRequested(request.getRequestId(), formIndex, sellerIndex));
        bp.add(ai);

        // Submit button — teruskan sellerIndex
        CircularButton sub = new CircularButton("➤", new Color(0, 150, 136), 32);
        sub.addActionListener(e -> handleSubmit(formIndex));
        bp.add(sub);

        row.add(bp, BorderLayout.EAST);
        return row;
    }

    private void handleSubmit(int fi) {
        JTextArea ta = fi == 1 ? form1Field : fi == 2 ? form2Field : form3Field;
        String v = ta.getText().trim();
        if (!v.isEmpty()) {
            // Teruskan sellerIndex ke controller
            controller.onSellerFormSubmit(request.getRequestId(), fi, v, sellerIndex);
        }
    }

    public void fillForm(int fi, String value) {
        JTextArea ta = fi == 1 ? form1Field : fi == 2 ? form2Field : form3Field;
        if (ta != null) ta.setText(value);
    }

    public ChatRequest getRequest() { return request; }

    public void updateRequest(ChatRequest r) {
        this.request = r;
        if (r.getProductExplanation() != null && form1Field.getText().isEmpty()) form1Field.setText(r.getProductExplanation());
        if (r.getPriceEstimation()    != null && form2Field.getText().isEmpty()) form2Field.setText(r.getPriceEstimation());
        if (r.getStockAvailability()  != null && form3Field.getText().isEmpty()) form3Field.setText(r.getStockAvailability());
    }
}