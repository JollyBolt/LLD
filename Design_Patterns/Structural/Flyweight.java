import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PROBLEM STATEMENT: Massive 3D Forest Rendering
 * * * Background:
 * You are building a game engine. A level designer wants to render a dense
 * forest with 1,000,000 trees. A standard Tree object contains heavy data:
 * a 3D polygon mesh, bark textures, and leaf textures (approx. 10MB per tree).
 * Creating 1,000,000 standard Tree objects will consume 10 Terabytes of RAM
 * and immediately crash the system.
 * * * Functional Requirements:
 * 1. The system must be able to spawn and draw hundreds of thousands of trees
 * at specific X and Y coordinates.
 * 2. The system must support different tree species (e.g., Oak, Pine).
 * * * Technical Constraints (Strict):
 * 1. Memory Optimization: Heavy data (mesh, texture, color) must only be loaded
 * into memory ONCE per species.
 * 2. State Separation: The unique data (X, Y coordinates) must remain separate
 * from the shared heavy data.
 * * --------------------------------------------------------------------------
 * LLD: FLYWEIGHT PATTERN
 * * CORE INTENT: Use sharing to support large numbers of fine-grained objects efficiently.
 * * KEY POINTERS:
 * 1. Intrinsic State (Shared/Heavy): Stored inside the Flyweight object.
 * 2. Extrinsic State (Unique/Light): Passed to the Flyweight methods by the client.
 * 3. Flyweight Factory: Essential for caching and reusing existing Flyweights.
 */

// --- 1. THE FLYWEIGHT (Intrinsic State - Shared & Heavy) ---
class TreeSpecies {
    private String name;
    private String color;
    private String heavyMeshAndTexture; // Simulating a massive 10MB data chunk

    public TreeSpecies(String name, String color, String texture) {
        this.name = name;
        this.color = color;
        this.heavyMeshAndTexture = texture;
        System.out.println("--> [EXPENSIVE OP] Loaded 10MB texture/mesh for " + name);
    }

    // The method takes Extrinsic State (X, Y) as parameters!
    public void draw(int x, int y) {
        System.out.println("Drawing a " + color + " " + name + " tree at (" + x + ", " + y + ")");
    }
}

class TreeFactory{
    private static Map<String, TreeSpecies> treeTypes = new HashMap<>();

    public static TreeSpecies getTreeSpecies(String name, String color, String texture){
        TreeSpecies result = treeTypes.get(name);

        if(result==null){
            result = new TreeSpecies(name,color,texture);
            treeTypes.put(name,result);
        }

        return result;
    }
}

// --- 3. THE CONTEXT (Extrinsic State - Unique & Light) ---
class Tree {
    private int x;
    private int y;
    // Holds a reference to the heavy, shared object
    private TreeSpecies species;

    public Tree(int x, int y, TreeSpecies species) {
        this.x = x;
        this.y = y;
        this.species = species;
    }

    public void draw() {
        // Passes its unique data to the shared object
        species.draw(x, y);
    }
}

class Forest {
    private List<Tree> trees = new ArrayList<>();

    public void plantTree(int x, int y, String name, String color, String texture) {
        TreeSpecies species = TreeFactory.getTreeSpecies(name, color, texture);
        Tree tree = new Tree(x, y, species);
        trees.add(tree);
    }

    public void draw() {
        for (Tree tree : trees) {
            tree.draw();
        }
    }
}

// --- 5. TESTER ---
public class Flyweight {
    public static void main(String[] args) {
        Forest forest = new Forest();

        // Planting 5 trees, but only 2 species!
        System.out.println("--- Planting Trees ---");
        forest.plantTree(10, 20, "Oak", "Green", "OakTexture.png");
        forest.plantTree(15, 25, "Oak", "Green", "OakTexture.png");
        forest.plantTree(50, 10, "Pine", "Dark Green", "PineTexture.png");
        forest.plantTree(55, 15, "Pine", "Dark Green", "PineTexture.png");
        forest.plantTree(60, 20, "Pine", "Dark Green", "PineTexture.png");

        System.out.println("\n--- Drawing Forest ---");
        forest.draw();

        System.out.println("\n[SYSTEM] Total Tree Objects in memory: 5");
        System.out.println("[SYSTEM] Total Heavy TreeSpecies in memory: 2 (Massive RAM savings!)");
    }
}
