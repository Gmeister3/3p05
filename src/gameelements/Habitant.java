package gameelements;

/**
 * Abstract base class for all village inhabitants.
 * <p>
 * A {@code Habitant} is an entity that lives in the village. All habitants
 * implement the {@link Damager} interface so they can participate in combat
 * (fighters attack, peasants may assist in defence).
 * </p>
 * <p>
 * Concrete subclasses are divided into:
 * <ul>
 *   <li>{@link Fighter} and its military subclasses ({@link Soldier}, {@link Archer},
 *       {@link Knight}, {@link Catapult})</li>
 *   <li>{@link Peasant} and its worker subclasses ({@link GoldMiner}, {@link IronMiner},
 *       {@link Lumberman})</li>
 * </ul>
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public abstract class Habitant implements Damager {

    /** Display name for this habitant type. */
    protected final String name;

    /**
     * Constructs a Habitant with the given name.
     *
     * @param name the display name of this habitant
     */
    protected Habitant(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of this habitant.
     *
     * @return the habitant's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a string representation of this habitant.
     *
     * @return descriptive string
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + name + "]";
    }
}
