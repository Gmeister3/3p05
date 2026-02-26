package gameelements;

/**
 * An Iron Mine produces iron over time.
 * <p>
 * Iron is used for constructing defensive buildings, training troops, and upgrades.
 * The production rate ({@code ironProd}) increases with each upgrade.
 * </p>
 *
 * <p><b>Build cost:</b> 80 Gold, 60 Iron, 40 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class IronMine extends Building {

    /** Iron produced per game tick. */
    private double ironProd;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 80;
    /** Build cost in iron. */
    public static final double COST_IRON   = 60;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 40;

    /**
     * Constructs an IronMine at level 1.
     */
    public IronMine() {
        super(150, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.ironProd = 8.0;
    }

    /**
     * Returns the iron produced per game tick.
     *
     * @return iron production rate
     */
    public double getIronProd() {
        return ironProd;
    }

    /**
     * Increases iron production when this mine is upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        ironProd += 6.0;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Iron Mine"
     */
    @Override
    public String getName() {
        return "Iron Mine";
    }
}
