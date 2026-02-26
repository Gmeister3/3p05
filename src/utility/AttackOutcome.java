package utility;

/**
 * Encapsulates the outcome of a combat engagement between an attacking {@link game.Army}
 * and a defending {@link game.Village}.
 * <p>
 * An {@code AttackOutcome} records whether the attack was successful, and if so,
 * the amount of each resource looted from the defender.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class AttackOutcome {

    /** Whether the attacking army won the engagement. */
    private final boolean success;

    /** The amount of gold looted from the defending village. */
    private final double newGold;

    /** The amount of iron looted from the defending village. */
    private final double newIron;

    /** The amount of lumber looted from the defending village. */
    private final double newLumber;

    /** Descriptive message summarising the battle result. */
    private final String message;

    /**
     * Constructs an {@code AttackOutcome} with full details.
     *
     * @param success   {@code true} if the attacker won
     * @param newGold   gold looted (0 if failed)
     * @param newIron   iron looted (0 if failed)
     * @param newLumber lumber looted (0 if failed)
     * @param message   human-readable battle summary
     */
    public AttackOutcome(boolean success, double newGold, double newIron,
                         double newLumber, String message) {
        this.success = success;
        this.newGold = newGold;
        this.newIron = newIron;
        this.newLumber = newLumber;
        this.message = message;
    }

    /**
     * Returns whether the attack was successful.
     *
     * @return {@code true} if the attack succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the amount of gold looted.
     *
     * @return gold gained from the attack
     */
    public double getNewGold() {
        return newGold;
    }

    /**
     * Returns the amount of iron looted.
     *
     * @return iron gained from the attack
     */
    public double getNewIron() {
        return newIron;
    }

    /**
     * Returns the amount of lumber looted.
     *
     * @return lumber gained from the attack
     */
    public double getNewLumber() {
        return newLumber;
    }

    /**
     * Returns the descriptive battle summary message.
     *
     * @return the battle result message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns a string representation of this attack outcome.
     *
     * @return string describing success and loot amounts
     */
    @Override
    public String toString() {
        return String.format("AttackOutcome{success=%b, gold=%.1f, iron=%.1f, lumber=%.1f}",
                success, newGold, newIron, newLumber);
    }
}
