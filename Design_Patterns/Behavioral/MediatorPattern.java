/**
 * PROBLEM STATEMENT: System UI Quick Settings Coordination
 * * * Background:
 * You are building the Quick Settings drop-down panel for an Android OS. 
 * The panel has several toggle buttons: Wi-Fi, Bluetooth, and Airplane Mode.
 * These buttons have complex interdependencies. 
 * * * Functional Requirements:
 * 1. If Airplane Mode is turned ON -> Wi-Fi and Bluetooth must automatically turn OFF.
 * 2. If Wi-Fi or Bluetooth is turned ON while Airplane Mode is active -> Airplane 
 * Mode must automatically turn OFF.
 * * * Technical Constraints (Strict):
 * 1. Zero Direct Coupling: The WifiToggle, BluetoothToggle, and AirplaneModeToggle 
 * classes must NOT hold references to each other. 
 * 2. Centralized Logic: All cross-component coordination rules must live in exactly 
 * one place.
 * * --------------------------------------------------------------------------
 * LLD: MEDIATOR PATTERN
 * * CORE INTENT: Define an object that encapsulates how a set of objects interact. 
 * Mediator promotes loose coupling by keeping objects from referring to each other explicitly.
 * * KEY POINTERS:
 * 1. Components only know about the Mediator.
 * 2. The Mediator knows about all the Components.
 * 3. Communication flows in a "Star" topology rather than a "Web".
 */

// --- 1. THE MEDIATOR INTERFACE (The Control Tower) ---
interface QuickSettingsMediator {
    // Components call this method to report a change
    void notify(Component sender, String event);
}

// --- 2. THE COMPONENT BASE CLASS ---
abstract class Component {
    protected QuickSettingsMediator mediator;

    public void setMediator(QuickSettingsMediator mediator) {
        this.mediator = mediator;
    }
}

// --- 3. CONCRETE COMPONENTS (The UI Toggles) ---
// Notice how NONE of these classes import or reference each other.
class WifiToggle extends Component {
    private boolean isOn = false;

    public void turnOn() {
        if (!isOn) {
            isOn = true;
            System.out.println("[UI] Wi-Fi turned ON");
            mediator.notify(this, "WIFI_ON");
        }
    }

    public void turnOff() {
        if (isOn) {
            isOn = false;
            System.out.println("[UI] Wi-Fi turned OFF");
        }
    }
}

class BluetoothToggle extends Component {
    private boolean isOn = false;

    public void turnOn() {
        if (!isOn) {
            isOn = true;
            System.out.println("[UI] Bluetooth turned ON");
            mediator.notify(this, "BT_ON");
        }
    }

    public void turnOff() {
        if (isOn) {
            isOn = false;
            System.out.println("[UI] Bluetooth turned OFF");
        }
    }
}

class AirplaneModeToggle extends Component {
    private boolean isOn = false;

    public void turnOn() {
        if (!isOn) {
            isOn = true;
            System.out.println("[UI] Airplane Mode turned ON");
            mediator.notify(this, "AIRPLANE_ON");
        }
    }

    public void turnOff() {
        if (isOn) {
            isOn = false;
            System.out.println("[UI] Airplane Mode turned OFF");
        }
    }
}

// --- 4. THE CONCRETE MEDIATOR (The Brain) ---
class SystemUIMediator implements QuickSettingsMediator {
    private WifiToggle wifi;
    private BluetoothToggle bluetooth;
    private AirplaneModeToggle airplaneMode;

    // The Mediator needs references to all components so it can control them
    public SystemUIMediator(WifiToggle w, BluetoothToggle b, AirplaneModeToggle a) {
        this.wifi = w;
        this.bluetooth = b;
        this.airplaneMode = a;

        // Link the components back to this mediator
        this.wifi.setMediator(this);
        this.bluetooth.setMediator(this);
        this.airplaneMode.setMediator(this);
    }

    @Override
    public void notify(Component sender, String event) {
        // Centralized coordination logic
        if (event.equals("AIRPLANE_ON")) {
            System.out.println("  -> [Mediator] Airplane mode activated. Forcing radios offline...");
            wifi.turnOff();
            bluetooth.turnOff();
        }
        else if (event.equals("WIFI_ON") || event.equals("BT_ON")) {
            System.out.println("  -> [Mediator] Radio manually activated. Disabling Airplane mode...");
            airplaneMode.turnOff();
        }
    }
}

// --- 5. TESTER (The Client) ---
public class MediatorPattern {
    public static void main(String[] args) {
        // 1. Create independent components
        WifiToggle wifi = new WifiToggle();
        BluetoothToggle bt = new BluetoothToggle();
        AirplaneModeToggle airplane = new AirplaneModeToggle();

        // 2. Create the mediator and wire them together
        SystemUIMediator qsPanel = new SystemUIMediator(wifi, bt, airplane);

        System.out.println("--- Scenario 1: Turning on Radios ---");
        wifi.turnOn();
        bt.turnOn();

        System.out.println("\n--- Scenario 2: User taps Airplane Mode ---");
        airplane.turnOn(); // This will trigger the mediator to shut down the radios!

        System.out.println("\n--- Scenario 3: User overrides by turning Wi-Fi back on ---");
        wifi.turnOn(); // This will trigger the mediator to shut off Airplane mode!
    }
}