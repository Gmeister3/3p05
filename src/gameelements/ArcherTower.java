package gameelements;

/**
 * An Archer Tower is a defensive building that fires arrows at attacking units.
 * <p>
 * It implements {@link Damager} to contribute to village defence. Its damage
 * increases with each upgrade level.
 * </p>
 *
 * <p><b>Build cost:</b> 60 Gold, 40 Iron, 30 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class ArcherTower extends Building implements Damager {

    /** Base damage dealt per tick at level 1. */
    private double damage;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 60;
    /** Build cost in iron. */
    public static final double COST_IRON   = 40;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 30;

    /**
     * Constructs an ArcherTower at level 1 with default statistics.
     */
    public ArcherTower() {
        super(200, COST_GOLD * 0.5, COST_IRON * 0.5, COST_LUMBER * 0.5);
        this.damage = 15.0;
    }

    /**
     * Returns the current damage output of this tower.
     *
     * @return damage per attack tick
     */
    @Override
    public double damage() {
        return damage;
    }

    /**
     * Resets or refreshes the tower's attack cycle.
     */
    @Override
    public void newOperation() {
        // Simulate resetting the firing cycle
        System.out.println(getName() + " is now targeting the enemy.");
    }

    /**
     * Increases the tower's damage when upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        damage += 10.0;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Archer Tower"
     */
    @Override
    public String getName() {
        return "Archer Tower";
    }

    /**
     * Returns the current damage field value (used internally).
     *
     * @return damage value
     */
    public double getDamage() {
        return damage;
    }
}
