/**
 * LLD: ADAPTER PATTERN
 * * CORE INTENT: Convert the interface of a class into another interface clients expect.
 * * KEY POINTERS:
 * 1. Wrapper: It wraps an existing object.
 * 2. Compatibility: Helps legacy code work with new requirements.
 */

// --- 1. THE TARGET INTERFACE (What the client expects) ---
interface WeightSensor {
    double getWeightInKilos();
}

// --- 2. THE ADAPTEE (Existing legacy class with different interface) ---
class LegacyPoundSensor {
    public double getWeightInPounds() {
        return 154.32; // Sample data
    }
}

// --- 3. THE ADAPTER (The Bridge) ---
class WeightAdapter implements WeightSensor {
    private LegacyPoundSensor legacySensor;

    //constructor which is taking the pound value
    public WeightAdapter(LegacyPoundSensor sensor) {
        this.legacySensor = sensor;
    }

    @Override
    public double getWeightInKilos() {
        // Conversion logic: lbs to kg
        return legacySensor.getWeightInPounds() * 0.453592;
    }
}

// --- 4. TESTER ---
public class Adapter {
    public static void main(String[] args) {
        LegacyPoundSensor oldSensor = new LegacyPoundSensor();

        // Client wants Kilos, but only has a Pound sensor.
        // We use the Adapter to bridge the gap.
        WeightSensor sensor = new WeightAdapter(oldSensor);

        System.out.println("Weight in Kilos: " + sensor.getWeightInKilos());
    }
}