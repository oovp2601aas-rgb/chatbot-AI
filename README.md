# Java Swing Chat Application with AI Assistant

## 📋 Project Overview

A complete buyer-seller chat application built with Java Swing, featuring an AI-powered assistant for sellers.

**Architecture:**
- **MVC Pattern**: Model (ChatRequest), View (UI Panels), Controller (ChatController)
- **Mediator Pattern**: ChatController coordinates all component communication
- **Service Layer**: SellerAIService provides AI functionality (separated from UI)

**Key Features:**
- Real-time buyer-seller messaging
- AI-powered seller response suggestions
- 3-field seller response system (Product, Price, Stock)
- Clean separation between UI and business logic

---

## 📁 Project Structure

```
antigravitychatbot/
└── src/
    ├── model/
    │   └── ChatRequest.java          # Data model for chat messages
    ├── service/
    │   └── SellerAIService.java      # AI service (mock rule-based)
    ├── controller/
    │   └── ChatController.java       # Mediator pattern implementation
    ├── ui/
    │   ├── Bubble.java               # Message bubble component
    │   ├── BuyerPanel.java           # Buyer UI panel
    │   └── SellerPanel.java          # Seller UI panel (3 forms)
    └── main/
        └── ChatApplication.java      # Main application entry point
```

---

## 🚀 How to Compile and Run

### Option 1: Using Command Line (javac)

```bash
# Navigate to project directory
cd "c:\Users\Aisyah Zahro Putri\OneDrive\Documents\antigravitychatbot"

# Compile all Java files
javac -d bin src/model/*.java src/service/*.java src/controller/*.java src/ui/*.java src/main/*.java

# Run the application
java -cp bin main.ChatApplication
```

### Option 2: Using NetBeans

1. Open NetBeans IDE
2. File → Open Project
3. Navigate to `antigravitychatbot` folder
4. Right-click project → Properties → Sources
5. Set Source/Binary Format to JDK 8 or higher
6. Right-click `ChatApplication.java` → Run File

### Option 3: Create NetBeans Project

1. File → New Project → Java → Java Application
2. Set project location to existing folder
3. Uncheck "Create Main Class" (we already have it)
4. Right-click project → Run

---

## 🎯 How to Use the Application

### For Buyers:
1. Type a message in the text field at the bottom
2. Click "Send" or press Enter
3. Your message appears in both buyer and seller chat windows

### For Sellers (Manual Mode):
1. Wait for buyer message to appear
2. Fill in the 3 response fields:
   - Product Explanation
   - Price Estimation
   - Stock Availability
3. Click "Send Response"

### For Sellers (AI-Assisted Mode):
1. Wait for buyer message to appear
2. Click the **"🤖 AI Suggest"** button
3. AI automatically fills all 3 fields with suggestions
4. **Edit the suggestions if needed** (you have full control)
5. Click "Send Response"

---

## 🤖 AI Integration Details

### Current Implementation: Mock Rule-Based AI

The `SellerAIService` uses **3 simple rules** based on keyword matching:

**Rule 1: Product Explanation**
- Detects keywords: laptop, phone, headphone, etc.
- Returns product descriptions based on detected category

**Rule 2: Price Estimation**
- Detects keywords: price, cost, cheap, budget, etc.
- Returns price ranges based on product type

**Rule 3: Stock Availability**
- Detects keywords: available, stock, urgent, color, etc.
- Returns stock status and shipping information

### Code Changes Made for AI Integration

**ONLY 2 minimal changes to existing architecture:**

1. **SellerPanel.java** - Added 1 button and 1 method:
   ```java
   // NEW: AI Suggest button
   private JButton aiSuggestButton;
   
   // NEW: Method to request AI suggestions
   private void requestAISuggestions() {
       controller.onAISuggestRequested();
   }
   
   // NEW: Method to fill form with AI data
   public void fillFormWithAISuggestions(String product, String price, String stock)
   ```

2. **ChatController.java** - Added 1 field and 1 method:
   ```java
   // NEW: AI service instance
   private SellerAIService aiService;
   
   // NEW: Handle AI suggestion requests
   public void onAISuggestRequested() {
       String[] responses = aiService.generateAllResponses(buyerMessage, requestId);
       sellerPanel.fillFormWithAISuggestions(responses[0], responses[1], responses[2]);
   }
   ```

**All other code remains unchanged!**

---

## 🔄 Future AI Upgrade Path

To replace mock AI with real AI (OpenAI, Gemini, Ollama, etc.):

### Step 1: Add Dependencies (Maven example)
```xml
<dependency>
    <groupId>com.theokanning.openai-gpt3-java</groupId>
    <artifactId>service</artifactId>
    <version>0.18.2</version>
</dependency>
```

### Step 2: Update SellerAIService.java
Replace the `generateXXX()` methods with API calls:

```java
private String generateProductExplanation(String message) {
    String prompt = "As a helpful seller, explain this product: " + message;
    return callOpenAI(prompt);
}

private String callOpenAI(String prompt) {
    OpenAiService service = new OpenAiService("YOUR_API_KEY");
    CompletionRequest request = CompletionRequest.builder()
        .model("gpt-3.5-turbo")
        .prompt(prompt)
        .maxTokens(150)
        .build();
    return service.createCompletion(request).getChoices().get(0).getText();
}
```

**No other code changes needed!** The UI and controller remain exactly the same.

---

## 📝 Architecture Compliance

✅ **All requirements met:**

- ✅ NO UI code changes (only added 1 button, no layout/color/font changes)
- ✅ AI logic completely separated from UI (SellerAIService class)
- ✅ MVC + Mediator pattern maintained
- ✅ NetBeans compatible
- ✅ Java Swing (no JavaFX or web conversion)
- ✅ Mock rule-based AI with clear upgrade path
- ✅ Well-commented code
- ✅ Minimal changes to existing architecture

---

## 🧪 Testing the Application

### Test Scenario 1: Basic Chat Flow
1. Buyer types: "Hello, I need a laptop"
2. Seller clicks "🤖 AI Suggest"
3. Verify all 3 fields are auto-filled
4. Seller clicks "Send Response"
5. Verify response appears in both chat windows

### Test Scenario 2: AI Keyword Detection
Try these buyer messages and observe AI suggestions:

- "I want to buy a smartphone" → Should suggest phone details
- "How much does it cost?" → Should suggest price range
- "Is it available in stock?" → Should suggest stock status
- "Do you have headphones?" → Should suggest headphone details

### Test Scenario 3: Manual Override
1. Buyer sends any message
2. Click "🤖 AI Suggest"
3. **Edit the AI suggestions manually**
4. Send response
5. Verify edited response is sent (not AI original)

---

## 📞 Support

For questions or issues:
- Check that Java JDK 8+ is installed
- Ensure all files are in correct package structure
- Verify NetBeans project settings if using IDE

---

**Built with Java Swing | MVC + Mediator Pattern | AI-Ready Architecture**
