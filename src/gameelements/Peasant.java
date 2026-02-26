package gameelements;

/**
 * Abstract base class for all worker (civilian) inhabitants.
 * <p>
 * A {@code Peasant} produces resources each game tick via the {@link #work()} method.
 * The production rate and level influence how much resource is generated.
 * Concrete subclasses ({@link GoldMiner}, {@link IronMiner}, {@link Lumberman})
 * produce different types of resources.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public abstract class Peasant extends Habitant {

    /** Base production per game tick. */
    protected double production;

    /** Current level of this peasant (determines production multiplier). */
    protected int level;

    /**
     * Constructs a Peasant with the given name and production rate.
     *
     * @param name       display name
     * @param production base production per tick
     */
    protected Peasant(String name, double production) {
        super(name);
        this.production = production;
        this.level      = 1;
    }

    /**
     * Performs one unit of work and returns the resource produced.
     *
     * @return a {@link Resource} representing what was produced this tick
     */
    public abstract Resource work();

    /**
     * Returns the base production rate of this peasant.
     *
     * @return production per tick
     */
    public double getProduction() {
        return production;
    }

    /**
     * Returns the effective production rate scaled by level.
     *
     * @return level-adjusted production rate
     */
    public double getEffectiveProduction() {
        return production * level;
    }

    /**
     * Returns the current level of this peasant.
     *
     * @return peasant level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Peasants deal minimal damage (they are not fighters).
     *
     * @return 1.0 (token damage)
     */
    @Override
    public double damage() {
        return 1.0;
    }

    /**
     * Peasants do not engage in combat; this is a no-op.
     */
    @Override
    public void newOperation() {
        // Peasants do not attack; work() is their primary operation
    }

    /**
     * Returns a string representation of this peasant.
     *
     * @return descriptive string with class, level, and production
     */
    @Override
    public String toString() {
        return String.format("%s[level=%d, prod=%.1f]",
                getClass().getSimpleName(), level, production);
    }
}
