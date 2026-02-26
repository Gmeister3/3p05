package gameelements;

/**
 * A Farm produces food that supports the village's population.
 * <p>
 * Each farm feeds a certain number of inhabitants; a larger population allows
 * more units to be trained. The population capacity increases with each upgrade.
 * </p>
 *
 * <p><b>Build cost:</b> 50 Gold, 30 Iron, 20 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Farm extends Building {

    /** Number of habitants this farm can support. */
    private int population;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 50;
    /** Build cost in iron. */
    public static final double COST_IRON   = 30;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 20;

    /**
     * Constructs a Farm at level 1 supporting an initial population.
     */
    public Farm() {
        super(100, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.population = 5;
    }

    /**
     * Returns the number of habitants this farm can support.
     *
     * @return the population capacity
     */
    public int getPopulation() {
        return population;
    }

    /**
     * Increases the supported population each time the farm is upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        population += 3;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Farm"
     */
    @Override
    public String getName() {
        return "Farm";
    }
}
