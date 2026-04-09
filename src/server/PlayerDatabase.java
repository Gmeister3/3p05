package server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// In-memory user database used by the server to authenticate players.
// Pre-populated with three default accounts for demonstration purposes. Passwords are
// stored as plain text (the assignment explicitly states that "text-based comparisons
// suffice" and that no sophisticated encryption is required).
// The map is backed by a ConcurrentHashMap so that the server's accept-thread and
// multiple ClientHandler threads can safely register or look up accounts simultaneously
// without external synchronization.
// The authentication flow follows a handshake-like protocol as required by Assignment 4:
// the client sends a LOGIN or REGISTER JSON message; the server checks this database and
// replies with LOGIN_OK / LOGIN_FAIL (or REGISTER_OK / REGISTER_FAIL) before any game
// commands are accepted.
public class PlayerDatabase {

    /**
     * Maps username → password for every registered player.
     * {@link ConcurrentHashMap} guarantees thread-safe reads and writes.
     */
    private final Map<String, String> accounts = new ConcurrentHashMap<>();

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Creates the database and seeds it with default accounts so the game can
     * be started without manual registration.
     */
    public PlayerDatabase() {
        // Default accounts (username / password)
        accounts.put("alice", "alice123");
        accounts.put("bob",   "bob456");
        accounts.put("admin", "admin");
    }

    /* ------------------------------------------------------------------ */
    /*  Public API                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Checks whether {@code username} and {@code password} match a stored account.
     *
     * @param username the login name supplied by the client
     * @param password the password supplied by the client
     * @return {@code true} if the credentials are valid, {@code false} otherwise
     */
    public boolean authenticate(String username, String password) {
        if (username == null || password == null) return false;
        String stored = accounts.get(username);
        return stored != null && stored.equals(password);
    }

    /**
     * Registers a new player account.
     *
     * @param username desired login name
     * @param password plain-text password
     * @return {@code true} if the account was created, {@code false} if the username
     *         was already taken
     */
    public boolean register(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }
        // putIfAbsent returns null only when the key was absent (i.e. insertion succeeded)
        return accounts.putIfAbsent(username, password) == null;
    }

    /**
     * Returns {@code true} if a username is already registered.
     *
     * @param username the username to check
     * @return whether the account exists
     */
    public boolean exists(String username) {
        return accounts.containsKey(username);
    }
}
