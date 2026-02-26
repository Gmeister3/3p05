package gameelements;

/**
 * An Archer is a ranged military unit that attacks from a distance.
 * <p>
 * Archers have moderate damage and lower hit points than melee units, but their
 * range makes them effective against fortifications.
 * </p>
 *
 * <p><b>Train cost:</b> 25 Gold, 5 Iron, 15 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Archer extends Fighter {

    /** Training cost in gold. */
    public static final double COST_GOLD   = 25;
    /** Training cost in iron. */
    public static final double COST_IRON   = 5;
    /** Training cost in lumber. */
    public static final double COST_LUMBER = 15;

    /**
     * Constructs an Archer with default combat statistics.
     */
    public Archer() {
        super("Archer", 18.0, 60.0);
    }
}
