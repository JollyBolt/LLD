/**
 * PROBLEM STATEMENT: Media Storage Scanner
 * * * Background:
 * You are writing a background service for an OS that traverses a tree of media
 * files (Directories, VideoFiles, AudioFiles). The product team constantly asks
 * for new features that process these files: first a Storage Calculator, then a
 * Malware Scanner, and next month, a Cloud Backup Sync engine.
 * * * Functional Requirements:
 * 1. The system must traverse a nested file structure.
 * 2. It must be able to calculate the total byte size of the tree.
 * 3. It must be able to scan specific file types for prohibited signatures.
 * * * Technical Constraints (Strict):
 * 1. Open/Closed Principle: You are strictly prohibited from adding new methods
 * like `scanForMalware()` or `syncToCloud()` to the core file classes. The data
 * classes must remain purely focused on holding data.
 * 2. Type-Specific Logic: The processing logic must know the exact type of file
 * it is dealing with (e.g., Audio vs. Video) without using massive `instanceof`
 * checks or casting.
 * * --------------------------------------------------------------------------
 * LLD: VISITOR PATTERN
 * * CORE INTENT: Represent an operation to be performed on the elements of an
 * object structure. Visitor lets you define a new operation without changing the
 * classes of the elements on which it operates.
 * * KEY POINTERS:
 * 1. Element Interface: Must have an `accept(Visitor v)` method.
 * 2. Visitor Interface: Must have a `visit(ConcreteElement e)` method for EACH
 * concrete type in the system.
 * 3. Double Dispatch: The element calls `visitor.visit(this)`, passing itself
 * securely to the visitor's type-specific method.
 */

import java.util.ArrayList;
import java.util.List;

// --- 1. THE VISITOR INTERFACE (The Operations) ---
// Notice how it has a specific method for every single concrete type.
interface StorageVisitor {
    void visit(VideoFile videoFile);
    void visit(AudioFile audioFile);
    void visit(DirectoryNode directoryNode);
}

// --- 2. THE ELEMENT INTERFACE (The Data Nodes) ---
interface StorageNode {
    // This is the key that lets the Visitor into the house
    void accept(StorageVisitor visitor);
}

// --- 3. CONCRETE ELEMENTS (The Data Structure) ---
// These classes only care about their own data. They have NO business logic.
class VideoFile implements StorageNode {
    private String fileName;
    private int sizeInMegabytes;

    public VideoFile(String fileName, int sizeInMegabytes) {
        this.fileName = fileName;
        this.sizeInMegabytes = sizeInMegabytes;
    }

    public int getSizeInMegabytes() { return sizeInMegabytes; }
    public String getFileName() { return fileName; }

    @Override
    public void accept(StorageVisitor visitor) {
        // DOUBLE DISPATCH: The file passes ITSELF to the visitor.
        // Because 'this' is a VideoFile, the compiler automatically routes
        // it to the `visit(VideoFile)` method in the visitor.
        visitor.visit(this);
    }
}

class AudioFile implements StorageNode {
    private String fileName;
    private int sizeInMegabytes;

    public AudioFile(String fileName, int sizeInMegabytes) {
        this.fileName = fileName;
        this.sizeInMegabytes = sizeInMegabytes;
    }

    public int getSizeInMegabytes() { return sizeInMegabytes; }
    public String getFileName() { return fileName; }

    @Override
    public void accept(StorageVisitor visitor) {
        visitor.visit(this);
    }
}

class DirectoryNode implements StorageNode {
    private String directoryName;
    private List<StorageNode> childrenNodes = new ArrayList<>();

    public DirectoryNode(String directoryName) {
        this.directoryName = directoryName;
    }

    public void addNode(StorageNode node) {
        childrenNodes.add(node);
    }

    public List<StorageNode> getChildrenNodes() { return childrenNodes; }

    @Override
    public void accept(StorageVisitor visitor) {
        visitor.visit(this);
    }
}

// --- 4. CONCRETE VISITORS (The Operations) ---

// Operation 1: Calculate Total Size
class SizeCalculatorVisitor implements StorageVisitor {
    private int totalSize = 0;

    @Override
    public void visit(VideoFile videoFile) {
        totalSize += videoFile.getSizeInMegabytes();
    }

    @Override
    public void visit(AudioFile audioFile) {
        totalSize += audioFile.getSizeInMegabytes();
    }

    @Override
    public void visit(DirectoryNode directoryNode) {
        // For directories, we just iterate and pass the visitor down the tree
        for (StorageNode childNode : directoryNode.getChildrenNodes()) {
            childNode.accept(this);
        }
    }

    public int getTotalSize() {
        return totalSize;
    }
}

// Operation 2: Scan for Malware (Added later without touching file classes!)
class MalwareScannerVisitor implements StorageVisitor {
    @Override
    public void visit(VideoFile videoFile) {
        System.out.println("[SCAN] Checking video codec signature for: " + videoFile.getFileName());
    }

    @Override
    public void visit(AudioFile audioFile) {
        System.out.println("[SCAN] Checking audio bitrate headers for: " + audioFile.getFileName());
    }

    @Override
    public void visit(DirectoryNode directoryNode) {
        System.out.println("[SCAN] Entering directory: " + directoryNode.getChildrenNodes().size() + " files found.");
        for (StorageNode childNode : directoryNode.getChildrenNodes()) {
            childNode.accept(this);
        }
    }
}

// --- 5. TESTER (The Client) ---
public class VisitorPattern {
    public static void main(String[] args) {
        // 1. Build the data structure (A simple file tree)
        DirectoryNode rootDirectory = new DirectoryNode("Internal Storage");
        rootDirectory.addNode(new VideoFile("vacation.mp4", 1500));
        rootDirectory.addNode(new AudioFile("podcast.mp3", 50));

        DirectoryNode downloadsDirectory = new DirectoryNode("Downloads");
        downloadsDirectory.addNode(new VideoFile("movie.mkv", 4000));

        rootDirectory.addNode(downloadsDirectory);

        // 2. Execute Operation 1: Size Calculation
        System.out.println("--- Executing Storage Calculation ---");
        SizeCalculatorVisitor sizeCalculator = new SizeCalculatorVisitor();
        rootDirectory.accept(sizeCalculator);
        System.out.println("Total Storage Used: " + sizeCalculator.getTotalSize() + " MB");

        // 3. Execute Operation 2: Malware Scan
        System.out.println("\n--- Executing Malware Scan ---");
        MalwareScannerVisitor malwareScanner = new MalwareScannerVisitor();
        rootDirectory.accept(malwareScanner);
    }
}