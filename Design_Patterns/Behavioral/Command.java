/**
 * PROBLEM STATEMENT: Text Editor Undo Engine
 * * Background: Build an editor that supports writing, deleting, and undoing actions.
 * * Constraints:
 * 1. Encapsulation: The document doesn't manage its own history.
 * 2. Uniform Execution: The history manager handles all actions uniformly.
 * * --------------------------------------------------------------------------
 * LLD: COMMAND PATTERN
 * * CORE INTENT: Encapsulate a request as an object, allowing you to parameterize
 * clients, queue or log requests, and support undoable operations.
 * * KEY POINTERS:
 * 1. Invoker: Asks the command to carry out the request.
 * 2. Command: Knows exactly how to execute (and undo) the action.
 * 3. Receiver: The object that actually performs the heavy lifting.
 */

import java.util.Stack;

// --- 1. THE RECEIVER (The actual text document) ---
// It only knows how to modify its own state.
class TextDocument {
    private StringBuilder content = new StringBuilder();

    public void insert(int index, String text) {
        content.insert(index, text);
    }

    public void delete(int startIndex, int length) {
        content.delete(startIndex, startIndex + length);
    }

    public String getContent() {
        return content.toString();
    }
}

// --- 2. THE COMMAND INTERFACE ---
interface EditorCommand {
    void execute();
    void undo();
}

// --- 3. CONCRETE COMMANDS ---
class WriteCommand implements EditorCommand {
    private TextDocument document;
    private String textToWrite;
    private int writePosition;

    public WriteCommand(TextDocument document, String textToWrite) {
        this.document = document;
        this.textToWrite = textToWrite;
        // Store the position so we know exactly where to undo it
        this.writePosition = document.getContent().length();
    }

    @Override
    public void execute() {
        document.insert(writePosition, textToWrite);
    }

    @Override
    public void undo() {
        document.delete(writePosition, textToWrite.length());
    }
}

class BackspaceCommand implements EditorCommand {
    private TextDocument document;
    private String deletedText;
    private int deletePosition;

    public BackspaceCommand(TextDocument document) {
        this.document = document;
        // In a real app, this would be the cursor position
        this.deletePosition = document.getContent().length() - 1;
    }

    @Override
    public void execute() {
        if (deletePosition >= 0) {
            // Save what we are about to delete so we can restore it later!
            deletedText = document.getContent().substring(deletePosition, deletePosition + 1);
            document.delete(deletePosition, 1);
        }
    }

    @Override
    public void undo() {
        if (deletedText != null) {
            document.insert(deletePosition, deletedText);
        }
    }
}

// --- 4. THE INVOKER (The History Manager) ---
// It manages the execution and keeps a stack for the Undo feature.
class CommandHistoryManager {
    private Stack<EditorCommand> history = new Stack<>();

    public void executeCommand(EditorCommand cmd) {
        cmd.execute();
        history.push(cmd); // Save to history after executing
    }

    public void undoLast() {
        if (!history.isEmpty()) {
            EditorCommand lastCmd = history.pop();
            lastCmd.undo();
        } else {
            System.out.println("Nothing to undo.");
        }
    }
}

// --- 5. TESTER (The Client) ---
public class Command {
    public static void main(String[] args) {
        TextDocument doc = new TextDocument();
        CommandHistoryManager history = new CommandHistoryManager();

        System.out.println("--- Typing 'Hello' ---");
        history.executeCommand(new WriteCommand(doc, "Hello"));
        System.out.println("Doc: " + doc.getContent());

        System.out.println("\n--- Typing ' World' ---");
        history.executeCommand(new WriteCommand(doc, " World"));
        System.out.println("Doc: " + doc.getContent());

        System.out.println("\n--- Hitting Backspace twice ---");
        history.executeCommand(new BackspaceCommand(doc));
        history.executeCommand(new BackspaceCommand(doc));
        System.out.println("Doc: " + doc.getContent());

        System.out.println("\n--- HITTING UNDO TWICE (Ctrl+Z) ---");
        history.undoLast(); // Undoes second backspace
        history.undoLast(); // Undoes first backspace
        System.out.println("Doc: " + doc.getContent());

        System.out.println("\n--- HITTING UNDO AGAIN (Ctrl+Z) ---");
        history.undoLast(); // Undoes ' World'
        System.out.println("Doc: " + doc.getContent());
    }
}