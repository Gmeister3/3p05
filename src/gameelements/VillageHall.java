package gameelements;

/**
 * The Village Hall is the central building of every village.
 * <p>
 * Its level controls the maximum upgrade level permitted for all other buildings.
 * A building may not be upgraded beyond the current Village Hall level.
 * </p>
 *
 * <p><b>Build cost:</b> 200 Gold, 100 Iron, 100 Lumber (only one per village)</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class VillageHall extends Building {

    /** Build cost in gold. */
    public static final double COST_GOLD   = 200;
    /** Build cost in iron. */
    public static final double COST_IRON   = 100;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 100;

    /**
     * Constructs a VillageHall at level 1.
     */
    public VillageHall() {
        super(500, COST_GOLD * 0.75, COST_IRON * 0.75, COST_LUMBER * 0.75);
    }

    /**
     * Returns the maximum level that other buildings may reach.
     * <p>
     * This equals the current level of the Village Hall.
     * </p>
     *
     * @return the maximum allowed level for dependent buildings
     */
    public int getMaxBuildingLevel() {
        return level;
    }

    /**
     * Applies the upgrade bonus: significantly increases hit points.
     */
    @Override
    protected void applyUpgradeBonus() {
        hitPoints += 150; // VillageHall is sturdier than other buildings
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Village Hall"
     */
    @Override
    public String getName() {
        return "Village Hall";
    }
}
