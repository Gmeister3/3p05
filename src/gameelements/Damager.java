package gameelements;

/**
 * Interface for game entities capable of dealing damage.
 * <p>
 * Both military units ({@link Fighter} subclasses) and defensive buildings
 * ({@link ArcherTower}, {@link Cannon}) implement this interface to participate
 * in combat calculations.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public interface Damager {

    /**
     * Returns the current damage value this entity deals per attack.
     *
     * @return the damage dealt
     */
    double damage();

    /**
     * Performs or refreshes this entity's attack operation.
     * <p>
     * For buildings this may reset a cooldown; for units it initiates an attack action.
     * </p>
     */
    void newOperation();
}
