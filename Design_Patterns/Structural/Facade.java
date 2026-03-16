/**
 * PROBLEM STATEMENT: Home Theater Automation System
 * * * Background:
 * You are integrating a smart Home Theater system. The system consists of
 * several distinct, complex components: a Projector, a Sound System, Room Lights,
 * and a DVD Player. To watch a movie, a user currently has to manually interact
 * with every single device in a specific order.
 * * * Functional Requirements:
 * 1. Watch Movie: The system must dim the lights, turn on the projector, turn on
 * the sound system, set the volume to 20, and start the DVD player.
 * 2. End Movie: The system must turn off the DVD player, turn off the sound
 * system, turn off the projector, and turn the lights back on.
 * * * Technical Constraints (Strict):
 * 1. Decoupling: The client code (e.g., a mobile app or voice assistant) must
 * NOT interact with the individual subsystem components directly.
 * 2. Unidirectional Awareness: The subsystem classes (Projector, Lights, etc.)
 * must remain completely independent. They cannot hold a reference to or
 * know about the unified control class.
 * 3. Unified Interface: You must provide a single, unified interface that
 * encapsulates the complex sequence of subsystem operations.
 * * --------------------------------------------------------------------------
 * LLD: FACADE PATTERN
 * * CORE INTENT: Provide a unified interface to a set of interfaces in a subsystem.
 * Facade defines a higher-level interface that makes the subsystem easier to use.
 * * * KEY POINTERS:
 * 1. It does NOT hide the subsystem classes completely (experts can still use them).
 * 2. It just provides a convenient "shortcut" for the most common tasks.
 */

// --- 1. COMPLEX SUBSYSTEM CLASSES ---
// (In a real system, these would likely be in separate files)
class Projector {
    public void on() { System.out.println("Projector is ON"); }
    public void off() { System.out.println("Projector is OFF"); }
}

class SoundSystem {
    public void on() { System.out.println("Sound System is ON"); }
    public void setVolume(int level) { System.out.println("Sound volume set to " + level); }
    public void off() { System.out.println("Sound System is OFF"); }
}

class Lights {
    public void dim() { System.out.println("Lights dimmed for movie mode"); }
    public void on() { System.out.println("Lights are fully ON"); }
}

class DvdPlayer {
    public void play(String movie) { System.out.println("Playing movie: " + movie); }
    public void stop() { System.out.println("Stopping movie"); }
    public void off() { System.out.println("DVD Player is OFF"); }
}

// --- 2. THE FACADE CLASS ---
class HomeTheaterFacade {
    private Projector projector;
    private SoundSystem soundSystem;
    private Lights lights;
    private DvdPlayer dvdPlayer;

    public HomeTheaterFacade(Projector p, SoundSystem s, Lights l, DvdPlayer d) {
        this.projector = p;
        this.soundSystem = s;
        this.lights = l;
        this.dvdPlayer = d;
    }

    public void watchMovie(String movie) {
        System.out.println("\n--- Get ready to watch a movie ---");
        projector.on();
        soundSystem.on();
        soundSystem.setVolume(20);
        lights.dim();
        dvdPlayer.play(movie);
    }

    public void endMovie() {
        System.out.println("\n--- Shutting movie theater down ---");
        projector.off();
        soundSystem.off();
        lights.on();
        dvdPlayer.off();
    }
}

// --- 3. TESTER (The Client) ---
public class Facade {
    public static void main(String[] args) {
        // 1. Instantiate the complex subsystem
        Projector p = new Projector();
        SoundSystem s = new SoundSystem();
        Lights l = new Lights();
        DvdPlayer d = new DvdPlayer();

        // 2. Client uses the Facade, shielding itself from the complexity
        HomeTheaterFacade homeTheater = new HomeTheaterFacade(p, s, l, d);

        // 3. Simple, unified method calls!
        homeTheater.watchMovie("Inception");
        homeTheater.endMovie();
    }
}
