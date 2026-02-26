package gameelements;

/**
 * A Lumber Mill produces lumber over time.
 * <p>
 * Lumber is required for constructing buildings and training certain units.
 * The production rate ({@code lumbProd}) increases with each upgrade.
 * </p>
 *
 * <p><b>Build cost:</b> 30 Gold, 10 Iron, 60 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class LumberMill extends Building {

    /** Lumber produced per game tick. */
    private double lumbProd;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 30;
    /** Build cost in iron. */
    public static final double COST_IRON   = 10;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 60;

    /**
     * Constructs a LumberMill at level 1.
     */
    public LumberMill() {
        super(120, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.lumbProd = 12.0;
    }

    /**
     * Returns the lumber produced per game tick.
     *
     * @return lumber production rate
     */
    public double getLumbProd() {
        return lumbProd;
    }

    /**
     * Increases lumber production when this mill is upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        lumbProd += 7.0;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Lumber Mill"
     */
    @Override
    public String getName() {
        return "Lumber Mill";
    }
}
