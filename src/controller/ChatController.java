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
 * ChatController - Mediator Pattern Implementation
 *
 * PERUBAHAN:
 * - onBuyerChoose sekarang menerima parameter quantity dari Bubble
 * - Cart menggunakan key "requestId-formIndex" agar setiap item unik
 * - Pesanan yang sudah di-Choose TIDAK berubah saat buyer kirim pesan baru
 */
public class ChatController {
    private BuyerPanel  buyerPanel;
    private SellerPanel sellerPanel;
    private List<ChatRequest> activeRequests;
    private int requestIdCounter;

 // 
    // key = "reqId-formIndex", value = {message, quantity}
    // LinkedHashMap menjaga urutan item masuk ke cart
    private Map<String, CartItem> cart = new LinkedHashMap<>();

    private SellerAIService aiService;

    // ──────────────────────────────────────────────────────────
    //  Inner class untuk menyimpan item cart
    // ──────────────────────────────────────────────────────────
    private static class CartItem {
        String message;
        int    quantity;
        double unitPrice; // harga satuan, 0 jika tidak terdeteksi

        CartItem(String message, int quantity, double unitPrice) {
            this.message   = message;
            this.quantity  = quantity;
            this.unitPrice = unitPrice;
        }
    }

    public ChatController() {
        this.activeRequests   = new ArrayList<>();
        this.requestIdCounter = 1;
        this.aiService        = new SellerAIService();
    }

    public void setBuyerPanel(BuyerPanel buyerPanel) {
        this.buyerPanel = buyerPanel;
        buyerPanel.setController(this);
    }

    public void setSellerPanel(SellerPanel sellerPanel) {
        this.sellerPanel = sellerPanel;
        sellerPanel.setController(this);
    }

    // ──────────────────────────────────────────────────────────
    //  Dipanggil saat buyer klik [ Choose ] pada seller bubble
    //  quantity sudah dihitung di Bubble sebelum dikirim ke sini
    // ──────────────────────────────────────────────────────────
    public void onBuyerChoose(int requestId, int formIndex, String message, int quantity) {
        onBuyerChoose(requestId, formIndex, message, quantity, 0.0);
    }

    public void onBuyerChoose(int requestId, int formIndex, String message, int quantity, double unitPrice) {
        String key = requestId + "-" + formIndex;
        cart.put(key, new CartItem(message, quantity, unitPrice));
        refreshSummary();
        System.out.println("[ChatController] Cart updated: REQ-" + requestId
                + " Form " + formIndex + " qty=" + quantity + " price=" + unitPrice);
    }

    // ──────────────────────────────────────────────────────────
    //  Overload untuk backward-compat (tanpa quantity → qty=1)
    // ──────────────────────────────────────────────────────────
    public void onBuyerChoose(int requestId, int formIndex, String message) {
        onBuyerChoose(requestId, formIndex, message, 1);
    }

    // ──────────────────────────────────────────────────────────
    //  Rebuild ringkasan pesanan dari isi cart
    // ──────────────────────────────────────────────────────────
    private void refreshSummary() {
        if (buyerPanel == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("\uD83E\uDDFE Ringkasan Pesanan Buyer\n");
        sb.append("--------------------------\n\n");

        // Tampilkan alamat jika sudah diisi
        String address = buyerPanel.getAddress();
        if (!address.isEmpty()) {
            sb.append("\uD83D\uDCCD Alamat: ").append(address).append("\n\n");
        }

        double grandTotal = 0;
        boolean hasPrice  = false;

        for (CartItem item : cart.values()) {
            // Ambil baris pertama pesan (judul item)
            String title = item.message.split("\n")[0].trim();

            sb.append("• ").append(title)
              .append("  x").append(item.quantity);

            if (item.unitPrice > 0) {
                double subtotal = item.unitPrice * item.quantity;
                grandTotal += subtotal;
                hasPrice = true;
                sb.append("  =  ").append(formatRupiah(subtotal));
            }

            sb.append("\n\n");
        }

        sb.append("--------------------------\n");

        if (hasPrice) {
            sb.append("Grand Total: ").append(formatRupiah(grandTotal)).append("\n\n");
        }

        sb.append("Silakan konfirmasi pesanan Anda \uD83D\uDE0A");

        buyerPanel.displayBuyerSummary(sb.toString());
    }

    /** Format angka ke Rupiah: Rp 15.000 */
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

    // ──────────────────────────────────────────────────────────
    //  Buyer kirim pesan baru
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

        sellerPanel.addRequest(request);

        System.out.println("[ChatController] New request: " + request.getRequestLabel() + " - " + message);
    }

    // ──────────────────────────────────────────────────────────
    //  Seller klik AI suggestion button
    // ──────────────────────────────────────────────────────────
    public void onAISuggestRequested(int requestId, int formIndex) {
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
        sellerPanel.fillFormField(requestId, formIndex, suggestion);

        System.out.println("[ChatController] AI suggestion for " + request.getRequestLabel() + " Form " + formIndex);
    }

    // ──────────────────────────────────────────────────────────
    //  Seller submit form field → replace waiting bubble
    // ──────────────────────────────────────────────────────────
    public void onSellerFormSubmit(int requestId, int formIndex, String value) {
        ChatRequest request = findRequestById(requestId);
        if (request == null) {
            System.out.println("[ChatController] Request not found: " + requestId);
            return;
        }

        switch (formIndex) {
            case 1:
                request.setProductExplanation(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 1: " + value);
                break;
            case 2:
                request.setPriceEstimation(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 2: " + value);
                break;
            case 3:
                request.setStockAvailability(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 3: " + value);
                break;
        }

        buyerPanel.replaceSpecificWaitingBubble(requestId, formIndex, value);

        if (request.isFullyResponded()) {
            request.setStatus(ChatRequest.Status.RESPONDED);
            System.out.println("[ChatController] " + request.getRequestLabel() + " fully responded");
        }
    }

    // ──────────────────────────────────────────────────────────
    //  Utilities
    // ──────────────────────────────────────────────────────────
    private ChatRequest findRequestById(int requestId) {
        for (ChatRequest r : activeRequests) {
            if (r.getRequestId() == requestId) return r;
        }
        return null;
    }

    public List<ChatRequest> getActiveRequests() {
        return new ArrayList<>(activeRequests);
    }

    public void clearAllChats() {
        activeRequests.clear();
        cart.clear();
        requestIdCounter = 1;
        if (buyerPanel  != null) buyerPanel.clearChat();
        if (sellerPanel != null) sellerPanel.clearAllRequests();
        System.out.println("[ChatController] All chats cleared");
    }
}