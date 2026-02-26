package gameelements;

/**
 * A Knight is a heavily armoured cavalry unit with high damage and hit points.
 * <p>
 * Knights are expensive but deliver superior combat performance, making them ideal
 * for attacking well-defended villages.
 * </p>
 *
 * <p><b>Train cost:</b> 50 Gold, 30 Iron, 10 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Knight extends Fighter {

    /** Training cost in gold. */
    public static final double COST_GOLD   = 50;
    /** Training cost in iron. */
    public static final double COST_IRON   = 30;
    /** Training cost in lumber. */
    public static final double COST_LUMBER = 10;

    /**
     * Constructs a Knight with default combat statistics.
     */
    public Knight() {
        super("Knight", 30.0, 150.0);
    }
}
