/**
 * LLD: ABSTRACT FACTORY PATTERN
 * * CORE INTENT: Create families of related objects without specifying concrete classes.
 * * KEY POINTERS:
 * 1. Consistency: Ensures products from the same family are used together.
 * 2. Hierarchy: It’s a "Factory of Factories."
 */

// --- 1. PRODUCT INTERFACES (The "What") ---
interface Chair { void sitOn(); }
interface Sofa { void lieOn(); }

// --- 2. MODERN FAMILY ---
class ModernChair implements Chair { public void sitOn() { System.out.println("Sitting on a Modern Chair."); } }
class ModernSofa implements Sofa { public void lieOn() { System.out.println("Lying on a Modern Sofa."); } }

// --- 3. VICTORIAN FAMILY ---
class VictorianChair implements Chair { public void sitOn() { System.out.println("Sitting on a Victorian Chair."); } }
class VictorianSofa implements Sofa { public void lieOn() { System.out.println("Lying on a Victorian Sofa."); } }

// --- 4. THE ABSTRACT FACTORY INTERFACE ---
interface FurnitureFactory {
    Chair createChair();
    Sofa createSofa();
}

// --- 5. CONCRETE FACTORIES (The "Themes") ---
class ModernFactory implements FurnitureFactory {
    public Chair createChair() { return new ModernChair(); }
    public Sofa createSofa() { return new ModernSofa(); }
}

class VictorianFactory implements FurnitureFactory {
    public Chair createChair() { return new VictorianChair(); }
    public Sofa createSofa() { return new VictorianSofa(); }
}

// --- 6. TESTER ---
public class AbstractFactory {
    public static void main(String[] args) {
        // Change this one line to 'new VictorianFactory()' to swap the whole family!
        FurnitureFactory factory = new ModernFactory();

        Chair chair = factory.createChair();
        Sofa sofa = factory.createSofa();

        chair.sitOn();
        sofa.lieOn();
    }
}