package gameelements;

/**
 * A Gold Mine produces gold over time.
 * <p>
 * Gold is the primary currency used for training troops and constructing buildings.
 * The production rate ({@code goldProd}) increases with each upgrade.
 * </p>
 *
 * <p><b>Build cost:</b> 100 Gold, 50 Iron, 50 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class GoldMine extends Building {

    /** Gold produced per game tick. */
    private double goldProd;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 100;
    /** Build cost in iron. */
    public static final double COST_IRON   = 50;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 50;

    /**
     * Constructs a GoldMine at level 1.
     */
    public GoldMine() {
        super(150, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.goldProd = 10.0;
    }

    /**
     * Returns the gold produced per game tick.
     *
     * @return gold production rate
     */
    public double getGoldProd() {
        return goldProd;
    }

    /**
     * Increases gold production when this mine is upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        goldProd += 8.0;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Gold Mine"
     */
    @Override
    public String getName() {
        return "Gold Mine";
    }
}
