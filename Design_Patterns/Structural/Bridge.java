/**
 * PROBLEM STATEMENT: Smart Home Control System
 * * Background:
 * Design the control software for a Smart Home ecosystem supporting various
 * electronic devices (TVs, Radios) and different interfaces/remotes
 * (Basic, Advanced).
 * * Constraints (Strict):
 * 1. Independent Scalability (Open-Closed): Adding a new device must require ZERO
 * changes to Remote classes. Adding a new remote requires ZERO changes to Devices.
 * 2. Prevent Class Explosion (M x N Rule): You cannot create dedicated Remote-Device
 * pairs (e.g., TvBasicRemote is NOT allowed).
 * 3. Behavioral Delegation: Devices expose primitives (enable, disable, setVolume).
 * Remotes build complex behaviors (togglePower, mute) using these primitives.
 * * --------------------------------------------------------------------------
 * LLD: BRIDGE PATTERN
 * * CORE INTENT: Decouple an abstraction from its implementation so that the two
 * can vary independently.
 * * KEY POINTERS:
 * 1. Solves Constraint #2 by turning M x N inheritance into M + N composition.
 * 2. Two Hierarchies: The "Abstraction" (RemoteControl) and "Implementation" (Device).
 */

// --- 1. THE IMPLEMENTATION (The "Heavy Lifting" Interface) ---
interface Device {
    boolean isEnabled();
    void enable();
    void disable();
    void setVolume(int percent);
}

// --- 2. CONCRETE IMPLEMENTATIONS (The specific devices) ---
class TV implements Device {
    private boolean on = false;
    private int volume = 30;

    public boolean isEnabled() { return on; }
    public void enable() { on = true; System.out.println("TV is ON."); }
    public void disable() { on = false; System.out.println("TV is OFF."); }
    public void setVolume(int percent) { this.volume = percent; System.out.println("TV volume set to " + volume); }
}

class Radio implements Device {
    private boolean on = false;
    private int volume = 10;

    public boolean isEnabled() { return on; }
    public void enable() { on = true; System.out.println("Radio is ON."); }
    public void disable() { on = false; System.out.println("Radio is OFF."); }
    public void setVolume(int percent) { this.volume = percent; System.out.println("Radio volume set to " + volume); }
}

// --- 3. THE ABSTRACTION (The "Control" layer) ---
class RemoteControl {
    // THE BRIDGE: Composition linking the two hierarchies
    protected Device device;

    public RemoteControl(Device device) {
        this.device = device;
    }

    public void togglePower() {
        if (device.isEnabled()) {
            device.disable();
        } else {
            device.enable();
        }
    }
}

// --- 4. REFINED ABSTRACTION (Extended control) ---
class AdvancedRemote extends RemoteControl {
    public AdvancedRemote(Device device) {
        super(device);
    }

    public void mute() {
        System.out.println("Muting device...");
        device.setVolume(0);
    }
}

// --- 5. TESTER ---
public class Bridge {
    public static void main(String[] args) {
        // We can mix and match any remote with any device!
        System.out.println("--- Testing Basic Remote with TV ---");
        Device tv = new TV();
        RemoteControl basicRemote = new RemoteControl(tv);
        basicRemote.togglePower(); // Turns TV on

        System.out.println("\n--- Testing Advanced Remote with Radio ---");
        Device radio = new Radio();
        AdvancedRemote advRemote = new AdvancedRemote(radio);
        advRemote.togglePower(); // Turns Radio on
        advRemote.mute();        // Mutes Radio
    }
}