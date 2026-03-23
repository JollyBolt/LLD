/**
 * PROBLEM STATEMENT: Dynamic Audio Transcription Engine
 * * * Background:
 * You are developing an Android app that records audio and sends it for text 
 * transcription. However, the environment is unpredictable. If the user has high 
 * battery and fast Wi-Fi, you want to use a heavy, highly accurate gRPC server 
 * running Faster-Whisper. If they are offline, you need to fall back to a 
 * lightweight on-device model. 
 * * * Functional Requirements:
 * 1. The system must take an audio file and return a transcribed string.
 * 2. The system must support switching the transcription algorithm at runtime 
 * based on network or battery states.
 * * * Technical Constraints (Strict):
 * 1. Open-Closed Principle: Adding a new transcription engine (e.g., a new 
 * Google Cloud API) must NOT require modifying the core AudioController class.
 * 2. No Conditionals: You are strictly prohibited from using if-else or switch 
 * statements inside the main processing method to choose the algorithm.
 * * --------------------------------------------------------------------------
 * LLD: STRATEGY PATTERN
 * * CORE INTENT: Define a family of algorithms, encapsulate each one, and make 
 * them interchangeable. Strategy lets the algorithm vary independently of
 * clients that use it.
 * * KEY POINTERS:
 * 1. Context: Maintains a reference to one of the concrete strategies and 
 * communicates with it via the strategy interface.
 * 2. Strategy Interface: Common to all supported algorithms.
 */

// --- 1. THE STRATEGY INTERFACE ---
interface TranscriptionStrategy {
    String transcribe(String audioFileName);
}

// --- 2. CONCRETE STRATEGIES (The Algorithms) ---
class FasterWhisperCloudStrategy implements TranscriptionStrategy {
    @Override
    public String transcribe(String audioFileName) {
        System.out.println("[gRPC Server] Streaming " + audioFileName + " to Faster-Whisper backend...");
        // Complex logic for gRPC streams goes here
        return "High-accuracy cloud transcription result.";
    }
}

class OnDeviceModelStrategy implements TranscriptionStrategy {
    @Override
    public String transcribe(String audioFileName) {
        System.out.println("[Local NPU] Processing " + audioFileName + " using lightweight on-device model...");
        // Logic for local tflite model goes here
        return "Fast, offline transcription result.";
    }
}

class FallbackApiStrategy implements TranscriptionStrategy {
    @Override
    public String transcribe(String audioFileName) {
        System.out.println("[REST API] Sending " + audioFileName + " to standard third-party REST endpoint...");
        // Logic for standard HTTP POST goes here
        return "Standard API transcription result.";
    }
}

// --- 3. THE CONTEXT (The class that USES the strategy) ---
class AudioTranscriptionController {
    // The controller doesn't know WHICH algorithm it has, only that it IS an algorithm.
    private TranscriptionStrategy strategy;

    // We can inject the default strategy via constructor
    public AudioTranscriptionController(TranscriptionStrategy defaultStrategy) {
        this.strategy = defaultStrategy;
    }

    // The Magic: We can swap the algorithm at runtime!
    public void setStrategy(TranscriptionStrategy newStrategy) {
        this.strategy = newStrategy;
        System.out.println("   -> [SYSTEM] Transcription engine swapped.");
    }

    // The core business method delegates the heavy lifting to the injected strategy
    public void processAudio(String audioFileName) {
        System.out.println("\nStarting audio processing pipeline...");
        String result = strategy.transcribe(audioFileName);
        System.out.println("Result: " + result);
    }
}

// --- 4. TESTER (The Client) ---
public class Strategy {
    public static void main(String[] args) {
        // 1. App boots up, Wi-Fi is strong. We default to the heavy cloud model.
        AudioTranscriptionController controller = new AudioTranscriptionController(new FasterWhisperCloudStrategy());
        controller.processAudio("meeting_recording.wav");

        // 2. User walks into an elevator, Wi-Fi drops. We catch the network 
        // change event and swap the strategy dynamically WITHOUT touching the controller's core logic.
        controller.setStrategy(new OnDeviceModelStrategy());
        controller.processAudio("quick_voice_note.wav");

        // 3. Device model fails or battery is too low, we swap to a standard API.
        controller.setStrategy(new FallbackApiStrategy());
        controller.processAudio("emergency_log.wav");
    }
}