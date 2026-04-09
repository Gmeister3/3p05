package server;

import game.Army;
import game.GameEngine;
import game.Player;
import game.Village;

// Encapsulates all game state that belongs to a single connected client session.
// Each time a player authenticates with the server a dedicated GameSession is created
// and attached to their ClientHandler. The session owns a GameEngine that drives the
// simulation for this player, and the authenticated Player model (village, army, score).
// Keeping game state per-session allows the server to support multiple simultaneous
// players without them interfering with each other's villages or armies.
public class GameSession {

    /** The game engine driving this player's simulation. */
    private final GameEngine engine;

    /** The authenticated player (model: village + army + score). */
    private final Player player;

    /** The username this session is authenticated as. */
    private final String username;

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Creates a new game session for an authenticated player.
     * The village name defaults to the player's username so that game state
     * is immediately identifiable in log messages.
     *
     * @param username    the authenticated login name
     * @param playerID    a unique integer ID assigned by the server
     */
    public GameSession(String username, int playerID) {
        this.username = username;
        this.engine   = new GameEngine();
        this.player   = new Player(playerID, username + "'s Village");

        // Register the player with the engine and generate NPC opponents
        engine.registerPlayer(player);
        engine.generateNpcVillages(5);
        engine.setRunning(true);
    }

    /* ------------------------------------------------------------------ */
    /*  Accessors                                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Returns the game engine for this session.
     *
     * @return the {@link GameEngine}
     */
    public GameEngine getEngine() {
        return engine;
    }

    /**
     * Returns the player model for this session.
     *
     * @return the {@link Player}
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the username associated with this session.
     *
     * @return the login name
     */
    public String getUsername() {
        return username;
    }

    /**
     * Convenience accessor for the player's village.
     *
     * @return the player's {@link Village}
     */
    public Village getVillage() {
        return player.getVillage();
    }

    /**
     * Convenience accessor for the player's army.
     *
     * @return the player's {@link Army}
     */
    public Army getArmy() {
        return player.getArmy();
    }

    /**
     * Advances the wall clock by one tick for this session's engine.
     */
    public void tick() {
        engine.tick();
    }
}
