package gameelements;

/**
 * A Lumberman is a worker that chops and collects lumber from forests.
 * <p>
 * Each game tick a Lumberman calls {@link #work()} and returns a {@link Lumber}
 * resource proportional to their {@link #getEffectiveProduction()} rate.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Lumberman extends Peasant {

    /**
     * Constructs a Lumberman at level 1 with a base production rate of 6 lumber per tick.
     */
    public Lumberman() {
        super("Lumberman", 6.0);
    }

    /**
     * Performs one work tick and produces lumber.
     *
     * @return a {@link Lumber} resource representing the lumber collected this tick
     */
    @Override
    public Resource work() {
        return new Lumber(getEffectiveProduction());
    }
}
