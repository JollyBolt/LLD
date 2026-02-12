/**
 * LLD: SINGLETON PATTERN
 * * CORE INTENT: Ensure a class has only one instance and provide a global point of access.
 * * KEY POINTERS:
 * 1. Private Constructor: Prevents direct 'new' calls.
 * 2. Private Static Instance: Holds the single copy.
 * 3. Public Static Method: The entry point (getInstance).
 */

// --- 1. THE PRODUCTION STANDARD (Double-Checked Locking) ---
class DatabaseConnection {
    // 'volatile' prevents memory write issues in multi-threading
    private static volatile DatabaseConnection instance;

    private DatabaseConnection() {
        // Pointer: Guard against Reflection API attacks
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method!");
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) { // First check (no locking)
            synchronized (DatabaseConnection.class) {
                if (instance == null) { // Second check (with locking)
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }
}

// --- 2. THE GOLD STANDARD (Enum) ---
// Note: Best for Java. Thread-safe, reflection-safe, and serialization-safe by default.
enum Logger {
    INSTANCE;

    public void log(String message) {
        System.out.println("Log: " + message);
    }
}

// --- 3. TESTER / REVISION AREA ---
public class Singleton {
    public static void main(String[] args) {
        // Accessing the Singleton
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();

        System.out.println("Are both DB instances same? " + (db1 == db2));

        // Accessing the Enum Singleton
        Logger.INSTANCE.log("Testing Singleton");
    }
}