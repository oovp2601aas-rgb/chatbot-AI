package controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.Timer;
import model.ChatRequest;
import service.SellerAIService;
import ui.BuyerPanel;
import ui.SellerPanel;

/**
 * ChatController - Mediator Pattern
 *
 * PERUBAHAN:
 * - Support 3 seller (sellerPanels List)
 * - Buyer broadcast pesan ke SEMUA seller
 * - Setiap seller reply secara independen ke buyer
 */
public class ChatController {

    private BuyerPanel        buyerPanel;
    private List<SellerPanel> sellerPanels  = new ArrayList<>();

    private List<ChatRequest> activeRequests;
    private int               requestIdCounter;

    private Map<String, CartItem> cart = new LinkedHashMap<>();

    private SellerAIService aiService;

    // ──────────────────────────────────────────────────────────
    private static class CartItem {
        String message;
        int    quantity;
        double unitPrice;
        CartItem(String msg, int qty, double price) {
            this.message = msg; this.quantity = qty; this.unitPrice = price;
        }
    }

    public ChatController() {
        this.activeRequests   = new ArrayList<>();
        this.requestIdCounter = 1;
        this.aiService        = new SellerAIService();
    }

    // ──────────────────────────────────────────────────────────
    //  Registrasi panel
    // ──────────────────────────────────────────────────────────
    public void setBuyerPanel(BuyerPanel bp) {
        this.buyerPanel = bp;
        bp.setController(this);
    }

    /** Tambah seller panel — dipanggil 3x dari ChatApplication */
    public void addSellerPanel(SellerPanel sp) {
        sellerPanels.add(sp);
        sp.setController(this);
    }

    // ──────────────────────────────────────────────────────────
    //  Buyer kirim pesan → broadcast ke SEMUA seller
    // ──────────────────────────────────────────────────────────
    public void onBuyerMessageSent(String message) {
        ChatRequest request = new ChatRequest(requestIdCounter++, message);
        request.setStatus(ChatRequest.Status.WAITING);
        activeRequests.add(request);

        buyerPanel.displayBuyerMessage(message);

        Timer t1 = new Timer(500,  e -> buyerPanel.displayWaitingMessage("seller data 1", request.getRequestId(), 1));
        Timer t2 = new Timer(1000, e -> buyerPanel.displayWaitingMessage("seller data 2", request.getRequestId(), 2));
        Timer t3 = new Timer(1500, e -> buyerPanel.displayWaitingMessage("seller data 3", request.getRequestId(), 3));
        t1.setRepeats(false); t1.start();
        t2.setRepeats(false); t2.start();
        t3.setRepeats(false); t3.start();

        // Broadcast ke semua seller
        for (SellerPanel sp : sellerPanels) {
            sp.addRequest(request);
        }

        System.out.println("[ChatController] Broadcast REQ-" + request.getRequestId()
                + " ke " + sellerPanels.size() + " seller: " + message);
    }

    // ──────────────────────────────────────────────────────────
    //  Seller submit form field
    //  sellerIndex: 0=Seller1, 1=Seller2, 2=Seller3
    // ──────────────────────────────────────────────────────────
    public void onSellerFormSubmit(int requestId, int formIndex, String value, int sellerIndex) {
        ChatRequest request = findRequestById(requestId);
        if (request == null) return;

        switch (formIndex) {
            case 1: request.setProductExplanation(value); break;
            case 2: request.setPriceEstimation(value);    break;
            case 3: request.setStockAvailability(value);  break;
        }

        // Kirim ke buyer dengan label nama seller
        String label = getSellerName(sellerIndex);
        String displayValue = "[" + label + "] " + value;
        buyerPanel.replaceSpecificWaitingBubble(requestId, formIndex, displayValue);

        if (request.isFullyResponded()) {
            request.setStatus(ChatRequest.Status.RESPONDED);
        }

        System.out.println("[ChatController] " + label + " submit REQ-"
                + requestId + " Form " + formIndex);
    }

    /** Backward compat — default seller 0 */
    public void onSellerFormSubmit(int requestId, int formIndex, String value) {
        onSellerFormSubmit(requestId, formIndex, value, 0);
    }

    // ──────────────────────────────────────────────────────────
    //  AI suggestion
    // ──────────────────────────────────────────────────────────
    public void onAISuggestRequested(int requestId, int formIndex, int sellerIndex) {
        ChatRequest request = findRequestById(requestId);
        if (request == null) return;

        SellerAIService.ResponseType type;
        switch (formIndex) {
            case 1: type = SellerAIService.ResponseType.PRODUCT_EXPLANATION; break;
            case 2: type = SellerAIService.ResponseType.PRICE_ESTIMATION;    break;
            case 3: type = SellerAIService.ResponseType.STOCK_AVAILABILITY;  break;
            default: return;
        }

        String suggestion = aiService.generateResponse(request.getBuyerMessage(), type, requestId);

        if (sellerIndex >= 0 && sellerIndex < sellerPanels.size()) {
            sellerPanels.get(sellerIndex).fillFormField(requestId, formIndex, suggestion);
        }
    }

    /** Backward compat */
    public void onAISuggestRequested(int requestId, int formIndex) {
        onAISuggestRequested(requestId, formIndex, 0);
    }

    // ──────────────────────────────────────────────────────────
    //  Buyer klik Choose
    // ──────────────────────────────────────────────────────────
    public void onBuyerChoose(int requestId, int formIndex, String message, int quantity, double unitPrice) {
        cart.put(requestId + "-" + formIndex, new CartItem(message, quantity, unitPrice));
        refreshSummary();
    }

    public void onBuyerChoose(int requestId, int formIndex, String message, int quantity) {
        onBuyerChoose(requestId, formIndex, message, quantity, 0.0);
    }

    public void onBuyerChoose(int requestId, int formIndex, String message) {
        onBuyerChoose(requestId, formIndex, message, 1, 0.0);
    }

    // ──────────────────────────────────────────────────────────
    //  Ringkasan pesanan buyer
    // ──────────────────────────────────────────────────────────
    private void refreshSummary() {
        if (buyerPanel == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83E\uDDFE Ringkasan Pesanan Buyer\n");
        sb.append("--------------------------\n\n");

        String address = buyerPanel.getAddress();
        if (!address.isEmpty()) {
            sb.append("\uD83D\uDCCD Alamat: ").append(address).append("\n\n");
        }

        double grandTotal = 0;
        boolean hasPrice  = false;

        for (CartItem item : cart.values()) {
            String title = item.message.split("\n")[0].trim();
            sb.append("• ").append(title).append("  x").append(item.quantity);

            if (item.unitPrice > 0) {
                double sub = item.unitPrice * item.quantity;
                grandTotal += sub;
                hasPrice = true;
                sb.append("  =  ").append(formatRupiah(sub));
            }
            sb.append("\n\n");
        }

        sb.append("--------------------------\n");
        if (hasPrice) sb.append("Grand Total: ").append(formatRupiah(grandTotal)).append("\n\n");
        sb.append("Silakan konfirmasi pesanan Anda \uD83D\uDE0A");

        buyerPanel.displayBuyerSummary(sb.toString());
    }

    // ──────────────────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────────────────
    private String getSellerName(int idx) {
        if (idx >= 0 && idx < sellerPanels.size()) {
            return sellerPanels.get(idx).getSellerName();
        }
        return "Seller " + (idx + 1);
    }

    private ChatRequest findRequestById(int id) {
        for (ChatRequest r : activeRequests) {
            if (r.getRequestId() == id) return r;
        }
        return null;
    }

    public List<ChatRequest> getActiveRequests() {
        return new ArrayList<>(activeRequests);
    }

    private String formatRupiah(double amount) {
        long val = Math.round(amount);
        String raw = String.valueOf(val);
        StringBuilder sb = new StringBuilder();
        int start = raw.length() % 3;
        if (start > 0) sb.append(raw, 0, start);
        for (int i = start; i < raw.length(); i += 3) {
            if (sb.length() > 0) sb.append(".");
            sb.append(raw, i, i + 3);
        }
        return "Rp " + sb.toString();
    }

    public void clearAllChats() {
        activeRequests.clear();
        cart.clear();
        requestIdCounter = 1;
        if (buyerPanel != null) buyerPanel.clearChat();
        for (SellerPanel sp : sellerPanels) sp.clearAllRequests();
        System.out.println("[ChatController] All chats cleared");
    }
}