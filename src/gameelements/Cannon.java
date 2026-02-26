package gameelements;

/**
 * A Cannon is a heavy defensive building that deals significant area damage.
 * <p>
 * Cannons have higher base damage than {@link ArcherTower}s but cost more to build.
 * Implements {@link Damager} to contribute to village defence.
 * </p>
 *
 * <p><b>Build cost:</b> 100 Gold, 80 Iron, 40 Lumber</p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Cannon extends Building implements Damager {

    /** Base damage dealt per tick at level 1. */
    private double damage;

    /** Build cost in gold. */
    public static final double COST_GOLD   = 100;
    /** Build cost in iron. */
    public static final double COST_IRON   = 80;
    /** Build cost in lumber. */
    public static final double COST_LUMBER = 40;

    /**
     * Constructs a Cannon at level 1 with default statistics.
     */
    public Cannon() {
        super(300, COST_GOLD * 0.5, COST_IRON * 0.5, COST_LUMBER * 0.5);
        this.damage = 30.0;
    }

    /**
     * Returns the current damage output of this cannon.
     *
     * @return damage per attack tick
     */
    @Override
    public double damage() {
        return damage;
    }

    /**
     * Resets or refreshes the cannon's attack cycle.
     */
    @Override
    public void newOperation() {
        System.out.println(getName() + " is reloading and targeting.");
    }

    /**
     * Increases the cannon's damage when upgraded.
     */
    @Override
    protected void applyUpgradeBonus() {
        damage += 20.0;
    }

    /**
     * Returns the display name of this building.
     *
     * @return "Cannon"
     */
    @Override
    public String getName() {
        return "Cannon";
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
