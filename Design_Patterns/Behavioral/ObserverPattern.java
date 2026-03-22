/**
 * PROBLEM STATEMENT: Real-Time Stock Market Ticker
 * * Background: A stock exchange receives price updates. Multiple UI dashboards 
 * and alert systems need to update instantly when prices change.
 * * Constraints:
 * 1. Strict Decoupling: The Subject (Exchange) doesn't know the concrete Observers.
 * 2. Dynamic Subscription: Observers can attach/detach at runtime.
 * 3. 1-to-N Broadcast: One state change alerts multiple independent listeners.
 * * --------------------------------------------------------------------------
 * LLD: OBSERVER PATTERN (Publish-Subscribe)
 * * CORE INTENT: Define a one-to-many dependency between objects so that when
 * one object changes state, all its dependents are notified and updated automatically.
 * * KEY POINTERS:
 * 1. Subject (Publisher): Maintains the list of listeners and sends notifications.
 * 2. Observer (Subscriber): Provides an update() interface to receive data.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

// --- 1. THE OBSERVER INTERFACE (The Subscribers) ---
interface Observer {
    void update(String stockSymbol, double price);
}

// --- 2. THE SUBJECT INTERFACE (The Publisher) ---
interface Subject {
    void attach(Observer o);
    void detach(Observer o);
    void notifyObservers(String stockSymbol, double price);
}

// --- 3. CONCRETE SUBJECT (The Core Data Holder) ---
class StockExchange implements Subject {
    // 1-to-N Relationship mapping
    private List<Observer> observers = new ArrayList<>();
    private Map<String, Double> stockPrices = new HashMap<>();

    @Override
    public void attach(Observer o) {
        observers.add(o);
        System.out.println("[SYSTEM] New observer attached.");
    }

    @Override
    public void detach(Observer o) {
        observers.remove(o);
        System.out.println("[SYSTEM] Observer detached.");
    }

    @Override
    public void notifyObservers(String stockSymbol, double price) {
        // Broadcast the event to everyone on the mailing list
        for (Observer observer : observers) {
            observer.update(stockSymbol, price);
        }
    }

    // Business Logic: The actual state change that triggers the notification
    public void setStockPrice(String stockSymbol, double price) {
        stockPrices.put(stockSymbol, price);
        System.out.println("\n[EXCHANGE] *** " + stockSymbol + " price updated to $" + price + " ***");
        notifyObservers(stockSymbol, price);
    }
}

// --- 4. CONCRETE OBSERVERS (The Listeners) ---
class MobileApp implements Observer {
    private String username;

    public MobileApp(String username) { this.username = username; }

    @Override
    public void update(String stockSymbol, double price) {
        System.out.println("  -> [Mobile App - " + username + "] UI updated: " + stockSymbol + " is now $" + price);
    }
}

class SmsAlertSystem implements Observer {
    private double alertThreshold;

    public SmsAlertSystem(double alertThreshold) { this.alertThreshold = alertThreshold; }

    @Override
    public void update(String stockSymbol, double price) {
        if (price > alertThreshold) {
            System.out.println("  -> [SMS ALERT] BEEP! " + stockSymbol + " crossed threshold! Current: $" + price);
        }
    }
}

// --- 5. TESTER (The Client) ---
public class ObserverPattern {
    public static void main(String[] args) {
        StockExchange nyse = new StockExchange();

        // 1. Create Observers
        Observer ishanMobile = new MobileApp("Ishan");
        Observer tradingBot = new MobileApp("AutoBot_01");
        Observer whaleAlert = new SmsAlertSystem(500.00);

        // 2. Attach Observers (Dynamic Subscription)
        nyse.attach(ishanMobile);
        nyse.attach(tradingBot);
        nyse.attach(whaleAlert);

        // 3. Trigger State Changes (Notice how the Exchange doesn't care WHO is listening)
        nyse.setStockPrice("AAPL", 150.25);
        nyse.setStockPrice("TSLA", 495.00);
        nyse.setStockPrice("TSLA", 505.50); // This will trigger the SMS alert!

        // 4. Detach an Observer at runtime
        System.out.println("\n[SYSTEM] AutoBot_01 is disconnecting...");
        nyse.detach(tradingBot);

        // 5. Trigger another change (AutoBot won't hear this one)
        nyse.setStockPrice("AAPL", 155.00);
    }
}

/*
Weak Reference Code implementation:


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class SafeStockExchange implements Subject {
    // We wrap our Observers in WeakReferences
    private List<WeakReference<Observer>> observers = new ArrayList<>();

    @Override
    public void attach(Observer o) {
        // Wrap the incoming observer before storing it
        observers.add(new WeakReference<>(o));
        System.out.println("[SYSTEM] New observer attached safely.");
    }

    @Override
    public void detach(Observer o) {
        // Manual detachment is still good practice, but no longer strictly
        // required to prevent a catastrophic memory leak.
        Iterator<WeakReference<Observer>> iterator = observers.iterator();
        while (iterator.hasNext()) {
            WeakReference<Observer> weakRef = iterator.next();
            Observer storedObserver = weakRef.get(); // Unwrap it

            // If the unwrapped object is the one we want to remove, drop it
            if (storedObserver == o) {
                iterator.remove();
                System.out.println("[SYSTEM] Observer manually detached.");
                break;
            }
        }
    }

    @Override
    public void notifyObservers(String stockSymbol, double price) {
        // We MUST use an Iterator here. If we use a standard for-loop and try to
        // remove items while looping, Java will throw a ConcurrentModificationException.
        Iterator<WeakReference<Observer>> iterator = observers.iterator();

        while (iterator.hasNext()) {
            WeakReference<Observer> weakRef = iterator.next();

            // .get() returns the real object if it exists, or NULL if the GC destroyed it
            Observer realObserver = weakRef.get();

            if (realObserver != null) {
                // The object is still alive! Send the update.
                realObserver.update(stockSymbol, price);
            } else {
                // The object was destroyed by the Garbage Collector!
                // The weakRef is now an empty shell. We must clean it up.
                System.out.println("[SYSTEM] Detected dead observer. Sweeping from list...");
                iterator.remove();
            }
        }
    }

    public void setStockPrice(String stockSymbol, double price) {
        System.out.println("\n[EXCHANGE] *** " + stockSymbol + " price updated to $" + price + " ***");
        notifyObservers(stockSymbol, price);
    }
}
 */