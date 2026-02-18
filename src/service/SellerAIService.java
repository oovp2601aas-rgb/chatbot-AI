package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import model.Product;

/**
 * SellerAIService - AI-powered assistant for sellers
 * 
 * ARCHITECTURE: Service Layer (separated from UI)
 * PURPOSE: Generate intelligent seller responses based on buyer messages
 * 
 * UPDATED: E-commerce simulation (Catalog, Stock, Price, Discount)
 */
public class SellerAIService {

    // In-memory catalog
    private List<Product> catalog;

    // Track pending orders for stock deduction: RequestID -> PendingOrder
    private Map<Integer, PendingOrder> pendingOrders;

    /**
     * Response types matching the 3 seller form fields
     */
    public enum ResponseType {
        PRODUCT_EXPLANATION,
        PRICE_ESTIMATION,
        STOCK_AVAILABILITY
    }

    // Internal helper class to track context
    private class PendingOrder {
        Product product;
        int quantity;

        PendingOrder(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    public SellerAIService() {
        this.catalog = new ArrayList<>();
        this.pendingOrders = new HashMap<>();
        initializeCatalog();
    }

    private void initializeCatalog() {
        catalog.add(new Product("Nasi Padang", 500.0, 10));
        catalog.add(new Product("Burger", 300.0, 15));
        catalog.add(new Product("Mango juice", 50.0, 20));
    }

    /**
     * Generate AI-suggested response for seller
     * 
     * @param buyerMessage The message from the buyer
     * @param responseType Which form field to generate response for
     * @param requestId    Optional request ID for context
     * @return Suggested response text
     */
    public String generateResponse(String buyerMessage, ResponseType responseType, int requestId) {
        if (buyerMessage == null || buyerMessage.trim().isEmpty()) {
            return "";
        }

        String message = buyerMessage.toLowerCase().trim();

        // Check for confirmation first (shared logic across types, but usually fits
        // best in Stock or Price)
        // For simplicity, if it's a confirmation, we'll return a confirmation message
        // regardless of type requested,
        // or specifically in STOCK_AVAILABILITY as that implies "reserve it".
        // Let's handle it inside specific generators for now.

        switch (responseType) {
            case PRODUCT_EXPLANATION:
                return generateProductExplanation(message);

            case PRICE_ESTIMATION:
                return generatePriceEstimation(message, requestId);

            case STOCK_AVAILABILITY:
                return generateStockAvailability(message, requestId);

            default:
                return "";
        }
    }

    /**
     * Helper: Detect product from message
     */
    private Product detectProduct(String message) {
        for (Product p : catalog) {
            // Simple containment check
            if (message.contains(p.getName().toLowerCase())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Helper: Extract quantity from message using Regex
     * Default: 1
     */
    private int extractQuantity(String message) {
        // Regex to find digits
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group());
            } catch (NumberFormatException e) {
                return 1;
            }
        }
        return 1; // Default
    }

    /**
     * Helper: Format currency
     */
    private String formatCurrency(double amount) {
        return String.format("$%.2f", amount);
    }

    private String generateProductExplanation(String message) {
        // Rule-based logic - keyword matching
        if (message.contains("padang") || message.contains("rice")) {
            return "Our authentic Padang Rice features steam rice with rich Rendang, flavorful Gulai Ayam, and spicy Sambal Ijo. A true Indonesian favorite!";
        } else if (message.contains("drink") || message.contains("juice") || message.contains("sweet")) {
            return "Refreshing beverages available in many flavors: Mango, Avocado, Orange, and Iced Lemon Tea. Perfect for any meal!";
        } else if (message.contains("cheap") || message.contains("price") || message.contains("affordable")) {
            return "We have budget-friendly meal boxes starting from just Rp 15,000. Quality food at the best prices!";
        } else if (message.contains("what") || message.contains("detail") || message.contains("info")) {
            return "This product features premium quality materials and comes with 1-year warranty. Let me know which specific product you're interested in!";
        } else {
            return "Sop Buah, 10k";
        }
    }

    /**
     * RULE 2: Price Estimation Generator
     * Suggests pricing based on buyer inquiry
     */
    private String generatePriceEstimation(String message, int requestId) {
        Product p = detectProduct(message);

        if (p != null) {
            int qty = extractQuantity(message);

            // Stock Check (Soft)
            if (qty > p.getStock()) {
                return "Sorry, we only have " + p.getStock() + " " + p.getName() + "(s) left in stock.";
            }

            // Calculate Total
            double unitPrice = p.getPrice();
            double subtotal = unitPrice * qty;
            double discount = 0;
            double total = subtotal;

            // Bulk Discount (> 5 items)
            if (qty >= 5) {
                discount = subtotal * 0.10; // 10%
                total = subtotal - discount;
            }

            // Store context for potential order
            pendingOrders.put(requestId, new PendingOrder(p, qty));

            StringBuilder sb = new StringBuilder();
            sb.append("You ordered: ").append(qty).append(" ").append(p.getName()).append("(s)\n");
            sb.append("Unit price: ").append(formatCurrency(unitPrice)).append("\n");
            sb.append("Subtotal: ").append(formatCurrency(subtotal)).append("\n");

            if (discount > 0) {
                sb.append("Bulk discount applied: 10% (-").append(formatCurrency(discount)).append(")\n");
            }
            sb.append("Final total: ").append(formatCurrency(total));
            return sb.toString();
        }

        // Fallback for unknown products
        return "Nasi Padang, 15k.";
    }

    /**
     * RULE 3: Stock Availability Generator
     * Provides stock status information & Handles Confirmation
     */
    private String generateStockAvailability(String message, int requestId) {
        // FEATURE 5: Deduct Stock After Confirmation
        if (message.contains("yes") || message.contains("confirm") || message.contains("ok")) {
            if (pendingOrders.containsKey(requestId)) {
                PendingOrder order = pendingOrders.get(requestId);

                // Double check stock
                if (order.quantity > order.product.getStock()) {
                    return "Sorry, stock has changed. Only " + order.product.getStock() + " available now.";
                }

                // Deduct
                order.product.deductStock(order.quantity);
                pendingOrders.remove(requestId); // Clear pending
                return "Confirmed! " + order.quantity + " " + order.product.getName()
                        + "(s) reserved. Remaining stock: " + order.product.getStock();
            } else {
                return "Nasi Goreng, 15k.";
            }
        }

        // Standard Stock Check
        Product p = detectProduct(message);
        if (p != null) {
            return "We have " + p.getStock() + " " + p.getName() + "(s) currently in stock. Ready to ship!";
        }

        return "Orange Juice, 15k.";
    }

    /**
     * Generate all three responses at once
     * Useful for auto-filling all seller form fields
     * 
     * @param buyerMessage The message from the buyer
     * @param requestId    Optional request ID
     * @return Array of [productExplanation, priceEstimation, stockAvailability]
     */
    public String[] generateAllResponses(String buyerMessage, int requestId) {
        return new String[] {
                generateResponse(buyerMessage, ResponseType.PRODUCT_EXPLANATION, requestId),
                generateResponse(buyerMessage, ResponseType.PRICE_ESTIMATION, requestId),
                generateResponse(buyerMessage, ResponseType.STOCK_AVAILABILITY, requestId)
        };
    }

    /*
     * ============================================================
     * FUTURE AI INTEGRATION PLACEHOLDER
     * ============================================================
     * 
     * To integrate real AI (OpenAI, Gemini, Ollama, etc.):
     * 
     * 1. Add dependency (Maven/Gradle):
     * - OpenAI: com.theokanning.openai-gpt3-java
     * - Google Gemini: google-cloud-aiplatform
     * - Local LLM: HTTP client for Ollama/LM Studio
     * 
     * 2. Replace generateXXX() methods with API calls:
     * 
     * private String generateProductExplanation(String message) {
     * String prompt =
     * "As a helpful seller, explain this product based on buyer question: " +
     * message;
     * return callAIAPI(prompt);
     * }
     * 
     * 3. Add API configuration:
     * - API keys (store in config file, not hardcoded)
     * - Model selection (gpt-4, gemini-pro, llama2, etc.)
     * - Temperature, max tokens, etc.
     * 
     * 4. Add error handling:
     * - API rate limits
     * - Network failures
     * - Fallback to rule-based responses
     * 
     * Example OpenAI integration:
     * 
     * private String callAIAPI(String prompt) {
     * try {
     * OpenAiService service = new OpenAiService("YOUR_API_KEY");
     * CompletionRequest request = CompletionRequest.builder()
     * .model("gpt-3.5-turbo")
     * .prompt(prompt)
     * .maxTokens(150)
     * .build();
     * return service.createCompletion(request)
     * .getChoices().get(0).getText();
     * } catch (Exception e) {
     * // Fallback to rule-based
     * return generateProductExplanation(message);
     * }
     * }
     */
}
