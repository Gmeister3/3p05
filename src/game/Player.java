package game;

/**
 * Represents a human player in the game.
 * <p>
 * Each player owns a {@link Village} and an {@link Army}. The player receives
 * notifications of important game events via the {@link #notify(Village)} method.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Player {

    /** Unique identifier for this player. */
    private final int playerID;

    /** The player's village. */
    private Village village;

    /** The player's army. */
    private Army army;

    /** The player's cumulative score. */
    private int score;

    /** Total number of successful attacks by this player. */
    private int attacksWon;

    /** Total number of attacks attempted by this player. */
    private int attacksTotal;

    /**
     * Constructs a Player with the given ID and a new village.
     *
     * @param playerID the unique player identifier
     * @param villageName the name for the player's village
     */
    public Player(int playerID, String villageName) {
        this.playerID    = playerID;
        this.village     = new Village(villageName);
        this.army        = new Army();
        this.score       = 0;
        this.attacksWon  = 0;
        this.attacksTotal = 0;
    }

    /**
     * Notifies the player of a significant event involving a village.
     * <p>
     * Typically called after a successful attack to display the battle summary.
     * </p>
     *
     * @param target the village involved in the event (attacker's or defender's)
     */
    public void notify(Village target) {
        System.out.println("[Player " + playerID + "] Notification: Village '"
                + target.getName() + "' state has changed.");
        System.out.println("  Current defence score: " + target.getDefenceScore());
    }

    /**
     * Awards points to the player and records the attack result.
     *
     * @param points   points to award
     * @param success  whether the attack was successful
     */
    public void recordAttack(int points, boolean success) {
        score += points;
        attacksTotal++;
        if (success) attacksWon++;
    }

    /**
     * Returns the player's unique identifier.
     *
     * @return player ID
     */
    public int getPlayerID() { return playerID; }

    /**
     * Returns the player's village.
     *
     * @return the {@link Village}
     */
    public Village getVillage() { return village; }

    /**
     * Returns the player's army.
     *
     * @return the {@link Army}
     */
    public Army getArmy() { return army; }

    /**
     * Returns the player's total score.
     *
     * @return score
     */
    public int getScore() { return score; }

    /**
     * Returns the number of successful attacks.
     *
     * @return attacks won
     */
    public int getAttacksWon() { return attacksWon; }

    /**
     * Returns the total number of attacks performed.
     *
     * @return total attacks
     */
    public int getAttacksTotal() { return attacksTotal; }

    /**
     * Returns a ranking summary string.
     *
     * @return formatted ranking info
     */
    public String getRankingSummary() {
        return String.format("Player %d | Score: %d | Attacks: %d/%d won",
                playerID, score, attacksWon, attacksTotal);
    }
}
