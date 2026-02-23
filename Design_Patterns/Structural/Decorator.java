Got it! You want the Decorator.java file updated so that the complete, formal problem statement is baked right into the header comments, just like we did for the Facade. This makes the file a standalone study guide.

/**
 * PROBLEM STATEMENT: Dynamic Pizza Ordering System
 * * * Background:
 * You are building the checkout system for a Pizza restaurant. Customers can
 * order base pizzas (like Margherita or FarmHouse) and customize them with any
 * number of extra toppings (Extra Cheese, Jalapenos). The system needs to
 * calculate the final cost and generate the full description of the customized pizza.
 * * * Functional Requirements:
 * 1. Base Items: The system must support Margherita ($200) and FarmHouse ($300).
 * 2. Add-ons: The system must support ExtraCheese ($50) and Jalapeno ($30).
 * 3. Aggregation: The system must correctly sum the total cost and concatenate
 * the item descriptions dynamically.
 * * * Technical Constraints (Strict):
 * 1. Prevent Class Explosion (No Subclassing for Combinations): You are strictly
 * prohibited from creating classes for specific combinations (e.g.,
 * MargheritaWithCheese, FarmHouseWithCheeseAndJalapeno).
 * 2. Runtime Modification: The system must allow adding multiple of the *same* * topping dynamically at runtime (e.g., double Extra Cheese).
 * 3. Transparent Wrapping: The wrapped object (Pizza + Toppings) must still be
 * treated as a standard `Pizza` object by the client code.
 * * * --------------------------------------------------------------------------
 * LLD: DECORATOR PATTERN
 * * CORE INTENT: Attach additional responsibilities to an object dynamically. 
 * Decorators provide a flexible alternative to subclassing for extending functionality.
 * * KEY POINTERS:
 * 1. IS-A and HAS-A: The Decorator implements the base interface AND holds a reference to it.
 * 2. Recursive Aggregation: Method calls cascade through all the wrappers.
 */

// --- 1. THE COMPONENT INTERFACE ---
interface Pizza {
    String getDescription();
    int getCost();
}

// --- 2. CONCRETE COMPONENTS (Base Items) ---
class Margherita implements Pizza {
    @Override
    public String getDescription() { return "Margherita"; }

    @Override
    public int getCost() { return 200; }
}

class FarmHouse implements Pizza {
    @Override
    public String getDescription() { return "FarmHouse"; }

    @Override
    public int getCost() { return 300; }
}

// --- 3. THE DECORATOR BASE CLASS (The Magic Wrapper) ---
// It IS a Pizza, and it HAS a Pizza.
abstract class ToppingDecorator implements Pizza {
    protected Pizza pizzaWrapper;

    public ToppingDecorator(Pizza pizza) {
        this.pizzaWrapper = pizza;
    }

    // Default delegation to the wrapped object
    @Override
    public String getDescription() { return pizzaWrapper.getDescription(); }

    @Override
    public int getCost() { return pizzaWrapper.getCost(); }
}

// --- 4. CONCRETE DECORATORS (The Add-ons) ---
class ExtraCheese extends ToppingDecorator {
    public ExtraCheese(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizzaWrapper.getDescription() + ", Extra Cheese";
    }

    @Override
    public int getCost() {
        return pizzaWrapper.getCost() + 50;
    }
}

class Jalapeno extends ToppingDecorator {
    public Jalapeno(Pizza pizza) { super(pizza); }

    @Override
    public String getDescription() {
        return pizzaWrapper.getDescription() + ", Jalapeno";
    }

    @Override
    public int getCost() {
        return pizzaWrapper.getCost() + 30;
    }
}

// --- 5. TESTER ---
public class Decorator {
    public static void main(String[] args) {
        // 1. Order a basic Margherita
        Pizza myOrder = new Margherita();

        // 2. Wrap it in Extra Cheese
        myOrder = new ExtraCheese(myOrder);

        // 3. Wrap it in another Extra Cheese (Double Cheese!)
        myOrder = new ExtraCheese(myOrder);

        // 4. Wrap it in Jalapenos
        myOrder = new Jalapeno(myOrder);

        // 5. Calculate final result (Cascades through all wrappers)
        System.out.println("Order: " + myOrder.getDescription());
        System.out.println("Total Cost: $" + myOrder.getCost());
    }
}