/**
 * PROBLEM STATEMENT: OTA (Over-The-Air) Firmware Update Pipeline
 * * * Background:
 * You are writing the system update service for an Android OS. The device can
 * be updated seamlessly in the background via Wi-Fi, or manually via a USB
 * drive in Recovery Mode.
 * * * Functional Requirements:
 * 1. The system must support different update delivery methods (Wi-Fi, USB).
 * 2. The exact sequence of the update must never be altered to prevent bricking
 * the device: Download/Load -> Verify -> Flash -> Reboot.
 * * * Technical Constraints (Strict):
 * 1. Algorithm Skeleton: The base class must dictate the exact order of execution.
 * 2. Security: The verification and reboot steps must be standardized and impossible
 * for subclasses to override.
 * 3. Hooks: Subclasses should optionally be able to back up user data before
 * flashing, but it shouldn't be mandatory.
 * * --------------------------------------------------------------------------
 * LLD: TEMPLATE METHOD PATTERN
 * * CORE INTENT: Define the skeleton of an algorithm in an operation, deferring
 * some steps to subclasses. Template Method lets subclasses redefine certain steps
 * of an algorithm without changing the algorithm's structure.
 * * KEY POINTERS:
 * 1. The Template Method itself MUST be marked `final` so subclasses can't break the order.
 * 2. Abstract methods: Steps the subclass MUST implement.
 * 3. Hook methods: Steps with an empty/default implementation that a subclass CAN override.
 */


// --- 1. THE ABSTRACT BASE CLASS (The Blueprint) ---
abstract class OtaUpdatePipeline {

    // THE TEMPLATE METHOD: Marked 'final' to prevent overriding.
    // This defines the strict algorithm skeleton to prevent bricking.
    public final void executeUpdate() {
        System.out.println("--- Starting System Update Sequence ---");

        fetchUpdatePackage();

        if (verifyChecksum()) {
            // This is a "Hook". It runs optionally if the subclass overrides it.
            onBeforeFlash();

            flashPartitions();
            rebootDevice();
        } else {
            System.out.println("[ERROR] Update aborted due to security failure.");
        }

        System.out.println("--- Update Sequence Complete ---\n");
    }

    // Standardized step: Security must be uniform. Subclasses cannot change this.
    private boolean verifyChecksum() {
        System.out.println("[SECURITY] Verifying SHA-256 cryptographic signature... Valid.");
        return true;
    }

    // Standardized step: Reboot logic is handled by the core OS.
    private void rebootDevice() {
        System.out.println("[SYSTEM] Issuing hardware reboot command...");
    }

    // Abstract steps: Subclasses MUST implement their own versions of these.
    protected abstract void fetchUpdatePackage();
    protected abstract void flashPartitions();

    // Hook step: Subclasses CAN override this if they want to, but it's not required.
    protected void onBeforeFlash() {
        // Default implementation does nothing.
    }
}

// --- 2. CONCRETE IMPLEMENTATION A (Background Wi-Fi Update) ---
class WiFiSeamlessUpdate extends OtaUpdatePipeline {
    private String serverUrl;

    public WiFiSeamlessUpdate(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    protected void fetchUpdatePackage() {
        System.out.println("[WI-FI] Downloading update payload from: " + serverUrl);
    }

    @Override
    protected void flashPartitions() {
        System.out.println("[SEAMLESS] Flashing inactive slot (Partition B) in the background.");
    }

    // Overriding the optional hook!
    @Override
    protected void onBeforeFlash() {
        System.out.println("[WI-FI] HOOK: Backing up user settings to cloud before flashing.");
    }
}

// --- 3. CONCRETE IMPLEMENTATION B (Manual USB Recovery Update) ---
class UsbRecoveryUpdate extends OtaUpdatePipeline {
    private String usbPath;

    public UsbRecoveryUpdate(String usbPath) {
        this.usbPath = usbPath;
    }

    @Override
    protected void fetchUpdatePackage() {
        System.out.println("[USB] Loading update payload directly from block device: " + usbPath);
    }

    @Override
    protected void flashPartitions() {
        System.out.println("[RECOVERY] Overwriting main partitions directly. DO NOT TURN OFF DEVICE.");
    }

    // Notice: We don't override the hook here. It just skips it quietly.
}

// --- 4. TESTER (The Client) ---
public class Template {
    public static void main(String[] args) {
        OtaUpdatePipeline nightlyBuild = new WiFiSeamlessUpdate("https://ota.android.com/nightly");
        // The Client just calls the Template Method. It doesn't orchestrate the steps.
        nightlyBuild.executeUpdate();

        OtaUpdatePipeline manualFix = new UsbRecoveryUpdate("/dev/block/sda1");
        manualFix.executeUpdate();
    }
}
