/**
 * LLD: PROTOTYPE PATTERN
 * * CORE INTENT: Create new objects by copying an existing object (the prototype).
 * * KEY POINTERS:
 * 1. Performance: Avoids expensive 'new' operations (like DB fetching or complex math).
 * 2. Independence: Changes to the clone don't affect the original.
 * 3. Deep Copy: Ensure nested objects are also cloned, not just referenced.
 */

// --- 1. THE PROTOTYPE INTERFACE ---
interface IPrototype {
    IPrototype clone();
}

// --- 2. CONCRETE PROTOTYPE ---
class GameCharacter implements IPrototype {
    private String name;
    private String weapon;
    private int health;

    public GameCharacter(String name, String weapon, int health) {
        this.name = name;
        this.weapon = weapon;
        this.health = health;
    }

    // Custom logic to modify the clone
    public void setName(String name) { this.name = name; }

    @Override
    public IPrototype clone() {
        // Deep Copy logic: Creating a brand new object with the same values
        return new GameCharacter(this.name, this.weapon, this.health);
    }

    @Override
    public String toString() {
        return "Character [Name=" + name + ", Weapon=" + weapon + ", Health=" + health + "]";
    }
}

// --- 3. TESTER ---
public class Prototype{
    public static void main(String[] args) {
        // 1. Create a "Base" character (The Prototype)
        GameCharacter soldierTemplate = new GameCharacter("Soldier", "Rifle", 100);

        // 2. Clone the template instead of using 'new' and setting all values again
        GameCharacter player1 = (GameCharacter) soldierTemplate.clone();
        player1.setName("Ishan_Warrior");

        GameCharacter player2 = (GameCharacter) soldierTemplate.clone();
        player2.setName("Enemy_AI");

        System.out.println("Original: " + soldierTemplate);
        System.out.println("Player 1: " + player1);
        System.out.println("Player 2: " + player2);
    }
}