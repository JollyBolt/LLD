/**
 * PROBLEM STATEMENT: RPG Game Save System
 * * * Background:
 * You are building a Role Playing Game (RPG). Players need to be able to save 
 * their game before a boss fight and reload that save if they die. The character 
 * has complex internal states (Health, Mana, Level, Coordinates).
 * * * Functional Requirements:
 * 1. The system must allow saving the character's exact state to a history stack.
 * 2. The system must allow restoring the character to any previous state.
 * * * Technical Constraints (Strict):
 * 1. Strict Encapsulation: The Save Manager (which holds the saves) must NEVER 
 * be able to look inside the save file or alter the player's stats directly. 
 * 2. Immutability: Once a save file is created, its data cannot be changed.
 * * --------------------------------------------------------------------------
 * LLD: MEMENTO PATTERN
 * * CORE INTENT: Without violating encapsulation, capture and externalize an 
 * object's internal state so that the object can be restored to this state later.
 * * KEY POINTERS:
 * 1. Originator: The object whose state we are tracking (The Player).
 * 2. Memento: The lockbox holding the state (The Save File).
 * 3. Caretaker: The object managing the history (The Save Manager).
 */

import java.util.Stack;

// --- 1. THE MEMENTO (The Lockbox / Save File) ---
// Notice there are NO setter methods. It is completely immutable.
class GameSave {
    private final int level;
    private final int health;
    private final String location;

    public GameSave(int level, int health, String location) {
        this.level = level;
        this.health = health;
        this.location = location;
    }

    // Only the Originator will use these getters to restore its state
    public int getLevel() { return level; }
    public int getHealth() { return health; }
    public String getLocation() { return location; }
}

// --- 2. THE ORIGINATOR (The Object being saved) ---
class PlayerCharacter {
    private int level = 1;
    private int health = 100;
    private String location = "Starting Village";

    public void play(String newLocation, int newLevel, int newHealth) {
        this.location = newLocation;
        this.level = newLevel;
        this.health = newHealth;
        System.out.println("[GAME] Player moved to " + location + " (Lvl: " + level + ", HP: " + health + ")");
    }

    // Creates the lockbox and hands it over
    public GameSave createSave() {
        System.out.println("[SYSTEM] Saving game state...");
        return new GameSave(level, health, location);
    }

    // Takes a lockbox and restores its own internal state
    public void restoreSave(GameSave save) {
        this.level = save.getLevel();
        this.health = save.getHealth();
        this.location = save.getLocation();
        System.out.println("[SYSTEM] Game Restored. Player is back at " + location + " (Lvl: " + level + ", HP: " + health + ")");
    }
}

// --- 3. THE CARETAKER (The Save Manager) ---
// It holds the Mementos, but it DOES NOT know what is inside them.
class SaveManager {
    private Stack<GameSave> saveHistory = new Stack<>();

    public void saveGame(PlayerCharacter player) {
        saveHistory.push(player.createSave());
    }

    public void loadLastSave(PlayerCharacter player) {
        if (!saveHistory.isEmpty()) {
            GameSave lastSave = saveHistory.pop();
            player.restoreSave(lastSave);
        } else {
            System.out.println("[SYSTEM] No save files found!");
        }
    }
}

// --- 4. TESTER (The Client) ---
public class MementoPattern {
    public static void main(String[] args) {
        PlayerCharacter player = new PlayerCharacter();
        SaveManager saveManager = new SaveManager();

        System.out.println("--- Starting Adventure ---");
        player.play("Dark Forest", 5, 80);

        // Save before the boss!
        saveManager.saveGame(player);

        System.out.println("\n--- Boss Fight ---");
        player.play("Dragon's Lair", 6, 10); // Took massive damage

        System.out.println("\n--- Player Died! Reloading... ---");
        saveManager.loadLastSave(player);
    }
}