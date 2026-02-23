/**
 * PROBLEM STATEMENT: File System Size Calculator
 * * Background: Represent files and directories. Calculate total size and show details.
 * * Constraints:
 * 1. Uniformity: Client treats single files and complex directories exactly the same.
 * 2. Infinite Nesting: Directories can hold files and other directories.
 * 3. Recursive Delegation: Directories calculate size by asking their children.
 * * --------------------------------------------------------------------------
 * LLD: COMPOSITE PATTERN
 * * CORE INTENT: Compose objects into tree structures to represent part-whole 
 * hierarchies. Lets clients treat individual objects and compositions uniformly.
 * * KEY POINTERS:
 * 1. Component Interface: The base contract for both leaves and composites.
 * 2. Leaf: The fundamental building block (has no children).
 * 3. Composite: The container (holds a list of Components).
 */

import java.util.ArrayList;
import java.util.List;

// --- 1. THE COMPONENT (Uniform Interface) ---
interface FileSystemComponent {
    int getSize();
    void showDetails();
}

// --- 2. THE LEAF (Individual Object) ---
class File implements FileSystemComponent {
    private String name;
    private int size;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void showDetails() {
        System.out.println("  File: " + name + " (" + size + " KB)");
    }
}

// --- 3. THE COMPOSITE (Container Object) ---
class Directory implements FileSystemComponent {
    private String name;
    // The Magic: It holds a list of the INTERFACE, not specific files
    private List<FileSystemComponent> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public void addComponent(FileSystemComponent component) {
        children.add(component);
    }

    public void removeComponent(FileSystemComponent component) {
        children.remove(component);
    }

    @Override
    public int getSize() {
        int totalSize = 0;
        // Recursive delegation
        for (FileSystemComponent component : children) {
            totalSize += component.getSize(); 
        }
        return totalSize;
    }

    @Override
    public void showDetails() {
        System.out.println("Directory: " + name);
        for (FileSystemComponent component : children) {
            component.showDetails();
        }
    }
}

// --- 4. TESTER ---
public class Composite {
    public static void main(String[] args) {
        // 1. Create Leaves (Files)
        FileSystemComponent file1 = new File("resume.pdf", 150);
        FileSystemComponent file2 = new File("photo.jpg", 2500);
        FileSystemComponent file3 = new File("script.sh", 5);

        // 2. Create Composites (Directories)
        Directory rootDir = new Directory("Root");
        Directory documentsDir = new Directory("Documents");

        // 3. Build the Tree
        documentsDir.addComponent(file1);
        documentsDir.addComponent(file2);
        
        rootDir.addComponent(documentsDir); // Directory inside Directory
        rootDir.addComponent(file3);        // File inside Directory

        // 4. Uniform Treatment: The client doesn't care if rootDir is a file or folder
        rootDir.showDetails();
        System.out.println("Total Size of Root: " + rootDir.getSize() + " KB");
    }
}