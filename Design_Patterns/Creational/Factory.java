/**
 * LLD: FACTORY PATTERN
 * * CORE INTENT: Define an interface for creating an object, but let subclasses 
 * decide which class to instantiate.
 * * KEY POINTERS:
 * 1. Abstraction: Use an Interface or Abstract class for the product.
 * 2. Hiding Logic: The client never uses the 'new' keyword for concrete classes.
 * 3. Scalability: To add a new type, you only change the Factory, not the Client code.
 */

// --- 1. THE PRODUCT INTERFACE ---
interface Notification {
    void notifyUser();
}

// --- 2. CONCRETE PRODUCTS ---
class EmailNotification implements Notification {
    public void notifyUser() {
        System.out.println("Sending an Email notification...");
    }
}

class SmsNotification implements Notification {
    public void notifyUser() {
        System.out.println("Sending an SMS notification...");
    }
}

// --- 3. THE FACTORY CLASS ---
class NotificationFactory {
    // The "Master" logic to decide which object to create
    public Notification createNotification(String type) {
        if (type == null || type.isEmpty()) return null;

        switch (type.toUpperCase()) {
            case "SMS":
                return new SmsNotification();
            case "EMAIL":
                return new EmailNotification();
            default:
                throw new IllegalArgumentException("Unknown notification type: " + type);
        }
    }
}

// --- 4. TESTER / REVISION AREA ---
public class Factory {
    public static void main(String[] args) {
        NotificationFactory factory = new NotificationFactory();

        // Client just asks for what they want by String/Enum
        Notification note = factory.createNotification("SMS");
        note.notifyUser();
    }
}