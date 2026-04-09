package network;

/**
 * Enumerates every message type that can appear in a client-server exchange.
 *
 * <p><b>Protocol overview</b><br>
 * Each JSON message sent over the TCP socket must contain a {@code "type"} field
 * whose value is the {@link #name()} of one of these constants.  The client sends
 * <em>request</em> types; the server replies with <em>response</em> types.</p>
 *
 * <pre>
 * CLIENT REQUEST  →  SERVER RESPONSE
 * ─────────────────────────────────────────────────────────
 * LOGIN           →  LOGIN_OK | LOGIN_FAIL
 * REGISTER        →  REGISTER_OK | REGISTER_FAIL
 * VIEW_VILLAGE    →  RESPONSE
 * BUILD           →  RESPONSE
 * TRAIN           →  RESPONSE
 * UPGRADE         →  RESPONSE
 * EXPLORE         →  RESPONSE
 * ATTACK          →  RESPONSE
 * COLLECT         →  RESPONSE
 * VIEW_RANKING    →  RESPONSE
 * VIEW_ARMY       →  RESPONSE
 * GENERATE_ARMY   →  RESPONSE  (NEW – A4)
 * TEST_VILLAGE    →  RESPONSE  (NEW – A4)
 * SAVE            →  RESPONSE
 * LOAD            →  RESPONSE
 * QUIT            →  RESPONSE
 * </pre>
 */
public enum MessageType {

    /* ── Authentication ─────────────────────────────────────────────────── */

    /** Client request: authenticate with username + password. */
    LOGIN,
    /** Server reply: authentication accepted. */
    LOGIN_OK,
    /** Server reply: authentication rejected. */
    LOGIN_FAIL,

    /** Client request: create a new account. */
    REGISTER,
    /** Server reply: registration accepted. */
    REGISTER_OK,
    /** Server reply: registration rejected (username taken). */
    REGISTER_FAIL,

    /* ── Game requests (client → server) ────────────────────────────────── */

    /** View the player's village status. */
    VIEW_VILLAGE,
    /** Build a new building. */
    BUILD,
    /** Train a new unit. */
    TRAIN,
    /** Upgrade an existing building. */
    UPGRADE,
    /** List available NPC targets. */
    EXPLORE,
    /** Attack a chosen NPC village. */
    ATTACK,
    /** Collect resources from workers. */
    COLLECT,
    /** View leaderboard and player ranking. */
    VIEW_RANKING,
    /** View army composition. */
    VIEW_ARMY,
    /**
     * Generate a detached army compatible with the player's current village for
     * testing defensive capabilities. (NEW – Assignment 4)
     */
    GENERATE_ARMY,
    /**
     * Run a series of generated compatible armies against the player's village
     * and return a defence success/fail score. (NEW – Assignment 4)
     */
    TEST_VILLAGE,
    /** Save the player's village to an XML file. */
    SAVE,
    /** Load the player's village from an XML file. */
    LOAD,
    /** Client is disconnecting; server should clean up the session. */
    QUIT,

    /* ── Generic server reply ────────────────────────────────────────────── */

    /**
     * Standard server response to any game-request message.
     * Carries {@code "status"} (OK | ERROR) and {@code "message"} fields.
     */
    RESPONSE
}
