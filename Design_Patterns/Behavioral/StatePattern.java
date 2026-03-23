/**
 * PROBLEM STATEMENT: Lock Screen Media Player
 * * * Background:
 * You are building the media playback controller for an Android OS. The device
 * has a physical "Play/Pause" button, a "Next" button, and a "Lock" button.
 * The behavior of these buttons changes entirely based on whether the phone is
 * currently Locked, Ready (Idle), or actively Playing audio.
 * * * Functional Requirements:
 * 1. If Locked: Play button does nothing. Next button does nothing. Lock button unlocks it.
 * 2. If Ready: Play button starts audio. Next button skips track. Lock button locks it.
 * 3. If Playing: Play button pauses audio. Next button skips track. Lock button locks it.
 * * * Technical Constraints (Strict):
 * 1. No Conditionals: You are strictly prohibited from using `if (state == LOCKED)`
 * or `switch (state)` statements inside the button click methods.
 * 2. State Encapsulation: Each state must be its own class that dictates what
 * the buttons do when that state is active.
 * 3. Internal Transitions: The states themselves must manage the transitions
 * to other states.
 * * --------------------------------------------------------------------------
 * LLD: STATE PATTERN
 * * CORE INTENT: Allow an object to alter its behavior when its internal state
 * changes. The object will appear to change its class.
 * * KEY POINTERS:
 * 1. Context: The primary object the client interacts with. It holds a reference
 * to the current State object and delegates all work to it.
 * 2. State Base Class: Declares the methods that all concrete states must implement.
 * Usually holds a back-reference to the Context to trigger state transitions.
 */

// --- 1. THE CONTEXT (The actual Media Player) ---
class MediaPlayer{
    private State state;
    private String currentTrack = "Track 1";

    public MediaPlayer(){
        this.state = new ReadyState(this);
    }

    public void changeState(State state){
        this.state = state;
    }

    public String getCurrentTrack(){ return currentTrack; }
    public void nextTrack() { currentTrack = "Track 2"; }

    // The Client only clicks the buttons. The Context delegates to the current state.
    public void clickLock() { state.clickLock(); }
    public void clickPlay() { state.clickPlay(); }
    public void clickNext() { state.clickNext(); }
}

// --- 2. THE STATE BASE CLASS ---
abstract class State{
    // Back-reference to the Context so the State can trigger transitions
    protected MediaPlayer player;

    public State(MediaPlayer player){
        this.player = player;
    }

    public abstract void clickLock();
    public abstract void clickPlay();
    public abstract void clickNext();
}

// --- 3. CONCRETE STATES ---

class LockedState extends State {
    public LockedState(MediaPlayer player) { super(player); }

    @Override
    public void clickLock() {
        System.out.println("[LOCKED] Phone unlocked. Transitioning to Ready.");
        player.changeState(new ReadyState(player));
    }

    @Override
    public void clickPlay() {
        System.out.println("[LOCKED] Ignored. Phone is locked.");
    }

    @Override
    public void clickNext() {
        System.out.println("[LOCKED] Ignored. Phone is locked.");
    }
}

class ReadyState extends State {
    public ReadyState(MediaPlayer player) { super(player); }

    @Override
    public void clickLock() {
        System.out.println("[READY] Phone locked. Transitioning to Locked.");
        player.changeState(new LockedState(player));
    }

    @Override
    public void clickPlay() {
        System.out.println("[READY] Starting playback. Transitioning to Playing.");
        player.changeState(new PlayingState(player));
    }

    @Override
    public void clickNext() {
        System.out.println("[READY] Skipping to next track: " + player.getCurrentTrack());
        player.nextTrack();
    }
}

class PlayingState extends State {
    public PlayingState(MediaPlayer player) { super(player); }

    @Override
    public void clickLock() {
        System.out.println("[PLAYING] Phone locked. (Audio continues in background)");
        player.changeState(new LockedState(player));
    }

    @Override
    public void clickPlay() {
        System.out.println("[PLAYING] Audio paused. Transitioning to Ready.");
        player.changeState(new ReadyState(player));
    }

    @Override
    public void clickNext() {
        player.nextTrack();
        System.out.println("[PLAYING] Skipping to next track: " + player.getCurrentTrack());
    }
}

// --- 4. TESTER (The Client) ---
public class StatePattern {
    public static void main(String[] args) {
        MediaPlayer player = new MediaPlayer(); // Starts in Ready state

        System.out.println("--- Scenario 1: Standard Playback ---");
        player.clickPlay(); // Goes to Playing
        player.clickNext(); // Stays in Playing, skips track
        player.clickPlay(); // Goes to Ready (Paused)

        System.out.println("\n--- Scenario 2: Locked Device ---");
        player.clickLock(); // Goes to Locked
        player.clickPlay(); // Ignored
        player.clickNext(); // Ignored

        System.out.println("\n--- Scenario 3: Unlocking ---");
        player.clickLock(); // Goes to Ready
        player.clickPlay(); // Goes to Playing
    }
}
