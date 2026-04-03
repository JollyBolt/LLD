/**
 * PROBLEM STATEMENT: Custom Media Playlist Traversal
 * * * Background:
 * You are building a media player application. The core data structure is a
 * `Playlist` that holds a list of `MediaTrack` objects. Users need to be able
 * to play the playlist sequentially or in shuffle mode.
 * * * Functional Requirements:
 * 1. The client must be able to iterate through the tracks one by one.
 * 2. The client must be able to change the traversal algorithm (Sequential vs. Shuffle).
 * * * Technical Constraints (Strict):
 * 1. Encapsulation: The Client must not know if the Playlist uses an ArrayList,
 * a LinkedList, or an Array internally.
 * 2. Single Responsibility: The Playlist class must only store data, not manage
 * the current playback cursor or shuffling logic.
 * * --------------------------------------------------------------------------
 * LLD: ITERATOR PATTERN
 * * CORE INTENT: Provide a way to access the elements of an aggregate object
 * sequentially without exposing its underlying representation.
 * * KEY POINTERS:
 * 1. Iterable (Aggregate): The data structure. It acts as a factory for iterators.
 * 2. Iterator: The object that tracks the current position and fetches the next item.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// --- 1. THE DATA MODEL ---
class MediaTrack {
    private String title;

    public MediaTrack(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}

// --- 2. THE ITERATOR INTERFACE ---
// Defines the standard way to loop through ANY collection.
interface TrackIterator {
    boolean hasNext();
    MediaTrack next();
}

// --- 3. CONCRETE ITERATORS (The Traversal Algorithms) ---

// Algorithm A: Standard sequential looping
class SequentialIterator implements TrackIterator {
    private List<MediaTrack> tracks;
    private int currentPosition = 0;

    public SequentialIterator(List<MediaTrack> tracks) {
        this.tracks = tracks;
    }

    @Override
    public boolean hasNext() {
        return currentPosition < tracks.size();
    }

    @Override
    public MediaTrack next() {
        if (hasNext()) {
            MediaTrack track = tracks.get(currentPosition);
            currentPosition++;
            return track;
        }
        return null;
    }
}

// Algorithm B: Shuffled looping
class ShuffleIterator implements TrackIterator {
    private List<MediaTrack> shuffledTracks;
    private int currentPosition = 0;

    public ShuffleIterator(List<MediaTrack> tracks) {
        // We create a randomized copy of the list specifically for this traversal
        this.shuffledTracks = new ArrayList<>(tracks);
        Collections.shuffle(this.shuffledTracks);
    }

    @Override
    public boolean hasNext() {
        return currentPosition < shuffledTracks.size();
    }

    @Override
    public MediaTrack next() {
        if (hasNext()) {
            MediaTrack track = shuffledTracks.get(currentPosition);
            currentPosition++;
            return track;
        }
        return null;
    }
}

// --- 4. THE AGGREGATE INTERFACE ---
interface Playlist {
    TrackIterator createSequentialIterator();
    TrackIterator createShuffleIterator();
}

// --- 5. THE CONCRETE AGGREGATE (The Data Structure) ---
class MusicPlaylist implements Playlist {
    // The internal representation is completely hidden from the client
    private List<MediaTrack> tracks = new ArrayList<>();

    public void addTrack(MediaTrack track) {
        tracks.add(track);
    }

    @Override
    public TrackIterator createSequentialIterator() {
        return new SequentialIterator(tracks);
    }

    @Override
    public TrackIterator createShuffleIterator() {
        return new ShuffleIterator(tracks);
    }
}

// --- 6. TESTER (The Client) ---
public class IteratorPattern {
    public static void main(String[] args) {
        MusicPlaylist myPlaylist = new MusicPlaylist();
        myPlaylist.addTrack(new MediaTrack("Song 1 - Intro"));
        myPlaylist.addTrack(new MediaTrack("Song 2 - The Anthem"));
        myPlaylist.addTrack(new MediaTrack("Song 3 - Ballad"));
        myPlaylist.addTrack(new MediaTrack("Song 4 - Outro"));

        // Scenario 1: Standard Playback
        System.out.println("--- Playing Sequentially ---");
        TrackIterator sequentialPlayer = myPlaylist.createSequentialIterator();

        // Notice how the client uses a standard while-loop.
        // It doesn't care about the internal ArrayList or cursor index.
        while (sequentialPlayer.hasNext()) {
            System.out.println("Playing: " + sequentialPlayer.next().getTitle());
        }

        // Scenario 2: Shuffle Playback
        System.out.println("\n--- Playing in Shuffle Mode ---");
        TrackIterator shufflePlayer = myPlaylist.createShuffleIterator();

        while (shufflePlayer.hasNext()) {
            System.out.println("Playing: " + shufflePlayer.next().getTitle());
        }
    }
}