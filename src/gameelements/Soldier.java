package gameelements;

/**
 * A Soldier is a basic melee infantry unit.
 * <p>
 * Soldiers are the cheapest military unit and serve as the backbone of early armies.
 * </p>
 *
 * <p><b>Train cost:</b> 20 Gold, 10 Iron, 5 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Soldier extends Fighter {

    /** Training cost in gold. */
    public static final double COST_GOLD   = 20;
    /** Training cost in iron. */
    public static final double COST_IRON   = 10;
    /** Training cost in lumber. */
    public static final double COST_LUMBER = 5;

    /**
     * Constructs a Soldier with default combat statistics.
     */
    public Soldier() {
        super("Soldier", 12.0, 80.0);
    }
}
