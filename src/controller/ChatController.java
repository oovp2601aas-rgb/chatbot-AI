package controller;

import model.ChatRequest;
import service.SellerAIService;
import ui.BuyerPanel;
import ui.SellerPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatController - Mediator Pattern Implementation
 * Updated for multi-session support
 * 
 * ROLE: Coordinates communication between BuyerPanel and SellerPanel
 * ARCHITECTURE: Mediator pattern - all components communicate through this
 * controller
 * 
 * NEW FEATURES:
 * - Multi-session request tracking
 * - Status management (PENDING, WAITING, RESPONDED)
 * - AI suggestions per form field
 * - Individual form field submission
 */
public class ChatController {
    private BuyerPanel buyerPanel;
    private SellerPanel sellerPanel;
    private List<ChatRequest> activeRequests;
    private int requestIdCounter;

    // AI Service
    private SellerAIService aiService;

    public ChatController() {
        this.activeRequests = new ArrayList<>();
        this.requestIdCounter = 1;
        this.aiService = new SellerAIService();
    }

    public void setBuyerPanel(BuyerPanel buyerPanel) {
        this.buyerPanel = buyerPanel;
        buyerPanel.setController(this);
    }

    public void setSellerPanel(SellerPanel sellerPanel) {
        this.sellerPanel = sellerPanel;
        sellerPanel.setController(this);
    }

    /**
     * Called when buyer sends a message
     * Creates new request and displays in both panels
     */
    public void onBuyerMessageSent(String message) {
        // Create new chat request
        ChatRequest request = new ChatRequest(requestIdCounter++, message);
        request.setStatus(ChatRequest.Status.WAITING);
        activeRequests.add(request);

        // Display in buyer panel
        buyerPanel.displayBuyerMessage(message);
        buyerPanel.displayWaitingMessage(request.getRequestLabel());

        // Add to seller dashboard
        sellerPanel.addRequest(request);

        // AI suggestions are now manual (seller clicks a button)
        // autoFillAISuggestions(request);

        System.out.println("[ChatController] New request: " + request.getRequestLabel() + " - " + message);
    }

    /**
     * Called when seller clicks the AI suggestion button for a field
     */
    public void onAISuggestRequested(int requestId, int formIndex) {
        ChatRequest request = findRequestById(requestId);
        if (request == null)
            return;

        String buyerMessage = request.getBuyerMessage();
        SellerAIService.ResponseType type;

        switch (formIndex) {
            case 1:
                type = SellerAIService.ResponseType.PRODUCT_EXPLANATION;
                break;
            case 2:
                type = SellerAIService.ResponseType.PRICE_ESTIMATION;
                break;
            case 3:
                type = SellerAIService.ResponseType.STOCK_AVAILABILITY;
                break;
            default:
                return;
        }

        String suggestion = aiService.generateResponse(buyerMessage, type, requestId);
        sellerPanel.fillFormField(requestId, formIndex, suggestion);

        System.out.println(
                "[ChatController] AI suggestion generated for " + request.getRequestLabel() + " Form " + formIndex);
    }

    /**
     * Called when seller submits an individual form field
     * 
     * @param requestId The request ID
     * @param formIndex Which form (1, 2, or 3)
     * @param value     The form value
     */
    public void onSellerFormSubmit(int requestId, int formIndex, String value) {
        // Find the request
        ChatRequest request = findRequestById(requestId);
        if (request == null) {
            System.out.println("[ChatController] Request not found: " + requestId);
            return;
        }

        // Update the appropriate field
        switch (formIndex) {
            case 1:
                request.setProductExplanation(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 1 submitted: " + value);
                break;
            case 2:
                request.setPriceEstimation(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 2 submitted: " + value);
                break;
            case 3:
                request.setStockAvailability(value);
                System.out.println("[ChatController] " + request.getRequestLabel() + " Form 3 submitted: " + value);
                break;
        }

        // Display the individual response in buyer chat
        String responseMessage = formatIndividualResponse(formIndex, value);
        buyerPanel.displaySellerResponse(responseMessage);

        // Check if all forms are filled
        if (request.isFullyResponded()) {
            request.setStatus(ChatRequest.Status.RESPONDED);
            System.out.println("[ChatController] " + request.getRequestLabel() + " fully responded");
        }
    }

    /**
     * Format individual form response for display
     */
    private String formatIndividualResponse(int formIndex, String value) {
        String prefix = "";
        switch (formIndex) {
            case 1:
                prefix = "📦 Product: ";
                break;
            case 2:
                prefix = "💰 Price: ";
                break;
            case 3:
                prefix = "📊 Stock: ";
                break;
        }
        return prefix + value;
    }

    /**
     * Find a request by ID
     */
    private ChatRequest findRequestById(int requestId) {
        for (ChatRequest request : activeRequests) {
            if (request.getRequestId() == requestId) {
                return request;
            }
        }
        return null;
    }

    /**
     * Get all active requests
     */
    public List<ChatRequest> getActiveRequests() {
        return new ArrayList<>(activeRequests);
    }

    /**
     * Clear all chats and requests
     */
    public void clearAllChats() {
        activeRequests.clear();
        requestIdCounter = 1;

        if (buyerPanel != null) {
            buyerPanel.clearChat();
        }
        if (sellerPanel != null) {
            sellerPanel.clearAllRequests();
        }

        System.out.println("[ChatController] All chats cleared");
    }
}
