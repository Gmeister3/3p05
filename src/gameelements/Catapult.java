package gameelements;

/**
 * A Catapult is a siege engine that deals massive damage to buildings.
 * <p>
 * Catapults have the highest damage of any unit but very low hit points and move slowly.
 * They are best used when supported by infantry and archers.
 * </p>
 *
 * <p><b>Train cost:</b> 80 Gold, 60 Iron, 50 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Catapult extends Fighter {

    /** Training cost in gold. */
    public static final double COST_GOLD   = 80;
    /** Training cost in iron. */
    public static final double COST_IRON   = 60;
    /** Training cost in lumber. */
    public static final double COST_LUMBER = 50;

    /**
     * Constructs a Catapult with default combat statistics.
     */
    public Catapult() {
        super("Catapult", 60.0, 40.0);
    }
}
