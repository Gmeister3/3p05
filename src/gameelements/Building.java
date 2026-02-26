package gameelements;

import exceptions.InsufficientResourcesException;
import exceptions.MaxLevelReachedException;

/**
 * Abstract base class for all village buildings.
 * <p>
 * Each building has hit points, a current level (1-5), and upgrade costs
 * denominated in Gold, Iron, and Lumber. The {@link #upgrade()} method
 * handles the common level-cap logic; subclasses define their own stat
 * improvements and cost schedules.
 * </p>
 * <p>
 * Implements {@link Updater} so all buildings support progressive upgrades.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public abstract class Building implements Updater {

    /** Maximum level any building can reach. */
    public static final int MAX_LEVEL = 5;

    /** Current hit points of the building. Reduced when under attack. */
    protected double hitPoints;

    /** Current level of the building (1–{@value #MAX_LEVEL}). */
    protected int level;

    /** Gold cost to upgrade to the next level. */
    protected double upgradeCostGold;

    /** Iron cost to upgrade to the next level. */
    protected double upgradeCostIron;

    /** Lumber cost to upgrade to the next level. */
    protected double upgradeCostLumber;

    /**
     * Constructs a Building with the specified parameters.
     *
     * @param hitPoints        initial hit points
     * @param upgradeCostGold  gold required per upgrade
     * @param upgradeCostIron  iron required per upgrade
     * @param upgradeCostLumber lumber required per upgrade
     */
    protected Building(double hitPoints, double upgradeCostGold,
                        double upgradeCostIron, double upgradeCostLumber) {
        this.hitPoints = hitPoints;
        this.level = 1;
        this.upgradeCostGold   = upgradeCostGold;
        this.upgradeCostIron   = upgradeCostIron;
        this.upgradeCostLumber = upgradeCostLumber;
    }

    /**
     * Returns the current hit points of this building.
     *
     * @return hit points remaining
     */
    public double getHitPoints() {
        return hitPoints;
    }

    /**
     * Sets the hit points of this building.
     *
     * @param hitPoints the new hit-point value
     */
    public void setHitPoints(double hitPoints) {
        this.hitPoints = hitPoints;
    }

    /**
     * Returns the current level of this building.
     *
     * @return current level (1–{@value #MAX_LEVEL})
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the gold cost to upgrade this building one level.
     *
     * @return upgrade cost in gold
     */
    public double getUpgradeCostGold() {
        return upgradeCostGold;
    }

    /**
     * Returns the iron cost to upgrade this building one level.
     *
     * @return upgrade cost in iron
     */
    public double getUpgradeCostIron() {
        return upgradeCostIron;
    }

    /**
     * Returns the lumber cost to upgrade this building one level.
     *
     * @return upgrade cost in lumber
     */
    public double getUpgradeCostLumber() {
        return upgradeCostLumber;
    }

    /**
     * Applies damage to this building, reducing its hit points.
     *
     * @param amount the amount of damage to apply
     */
    public void takeDamage(double amount) {
        this.hitPoints = Math.max(0, this.hitPoints - amount);
    }

    /**
     * Returns whether this building has been destroyed (zero hit points).
     *
     * @return {@code true} if the building is destroyed
     */
    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    /**
     * Upgrades this building by one level, improving its stats.
     * <p>
     * Subclasses override {@link #applyUpgradeBonus()} to apply stat changes.
     * Resource deduction is handled by the caller (e.g., {@link game.Village}).
     * </p>
     *
     * @throws MaxLevelReachedException       if already at maximum level
     * @throws InsufficientResourcesException not thrown here; caller is responsible
     */
    @Override
    public void upgrade() throws MaxLevelReachedException, InsufficientResourcesException {
        if (level >= MAX_LEVEL) {
            throw new MaxLevelReachedException(getClass().getSimpleName(), MAX_LEVEL);
        }
        level++;
        hitPoints += 50; // base hp increase per level
        applyUpgradeBonus();
    }

    /**
     * Hook method called by {@link #upgrade()} after the level is incremented.
     * Subclasses implement this to improve their specific stats.
     */
    protected abstract void applyUpgradeBonus();

    /**
     * Returns a human-readable name for this building type.
     *
     * @return the building type name
     */
    public abstract String getName();

    /**
     * Returns a string representation of this building.
     *
     * @return descriptive string with name, level, and hit points
     */
    @Override
    public String toString() {
        return String.format("%s[level=%d, hp=%.1f]", getName(), level, hitPoints);
    }
}
