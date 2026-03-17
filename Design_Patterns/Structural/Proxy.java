/**
 * PROBLEM STATEMENT: Cloud Audio Transcription Cache
 * * * Background:
 * You are building a client application that connects to a heavy Voice-to-Text
 * backend (e.g., a gRPC server running a model like Faster-Whisper). Transcribing
 * audio is extremely resource-intensive and network calls are expensive.
 * * * Functional Requirements:
 * 1. The system must accept an audio file ID and return its text transcription.
 * 2. It must simulate the delay of a heavy backend process.
 * * * Technical Constraints (Strict):
 * 1. Caching (Performance): The system must never send the same audio ID to the
 * remote server more than once. Subsequent requests for the same ID must return
 * instantly from local memory.
 * 2. Lazy Initialization: The connection to the heavy backend should NOT be
 * established until the very first time a transcription is actually needed.
 * 3. Interface Uniformity: The client code must not know whether it is receiving
 * data from the Cache or the Remote Server. It must use a single interface.
 * * --------------------------------------------------------------------------
 * LLD: PROXY PATTERN
 * * CORE INTENT: Provide a surrogate or placeholder for another object to
 * control access to it.
 * * KEY POINTERS:
 * 1. The Proxy and the Real Subject share the exact same interface.
 * 2. The Proxy intercepts calls to do "housekeeping" (caching, auth, logging)
 * before or after delegating to the Real Subject.
 */

import java.util.HashMap;
import java.util.Map;

// --- 1. THE SUBJECT INTERFACE ---
// Both the Proxy and the Real Object will implement this.
interface TranscriptionService {
    String transcribe(String audioId);
}

// --- 2. THE REAL SUBJECT (The Heavy Object) ---
// This represents your actual connection to the backend.
class GrpcTranscriptionBackend implements TranscriptionService {

    public GrpcTranscriptionBackend() {
        // Simulating heavy startup time (e.g., loading models, establishing sockets)
        System.out.println("--> [SYSTEM] Booting up heavy gRPC connection to Whisper backend...");
        simulateNetworkDelay(1500);
    }

    @Override
    public String transcribe(String audioId) {
        System.out.println("--> [NETWORK] Uploading " + audioId + " to server and computing...");
        simulateNetworkDelay(2000);
        return "Transcribed text for: " + audioId;
    }

    private void simulateNetworkDelay(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { }
    }
}

// --- 3. THE PROXY (The Smart Wrapper) ---
class TranscriptionProxy implements TranscriptionService {
    // Reference to the real service (Lazy loaded)
    private GrpcTranscriptionBackend realBackend;

    // The Cache
    private Map<String, String> cache = new HashMap<>();

    @Override
    public String transcribe(String audioId) {
        // 1. Check the cache first (Caching Proxy)
        if (cache.containsKey(audioId)) {
            System.out.println("[PROXY] Cache HIT! Returning instant result for: " + audioId);
            return cache.get(audioId);
        }

        // 2. Lazy Initialization (Virtual Proxy)
        if (realBackend == null) {
            realBackend = new GrpcTranscriptionBackend();
        }

        // 3. Delegate to the real object and cache the result
        System.out.println("[PROXY] Cache MISS. Delegating to heavy backend...");
        String result = realBackend.transcribe(audioId);
        cache.put(audioId, result);

        return result;
    }
}

// --- 4. TESTER (The Client) ---
public class Proxy {
    public static void main(String[] args) {
        // The client only talks to the interface. It doesn't know about the proxy or cache.
        TranscriptionService service = new TranscriptionProxy();

        System.out.println("--- Request 1: audio_123.wav ---");
        System.out.println("Result: " + service.transcribe("audio_123.wav") + "\n");

        System.out.println("--- Request 2: audio_456.wav ---");
        System.out.println("Result: " + service.transcribe("audio_456.wav") + "\n");

        System.out.println("--- Request 3: audio_123.wav (AGAIN) ---");
        // This will be instantaneous and won't hit the network
        System.out.println("Result: " + service.transcribe("audio_123.wav") + "\n");
    }
}