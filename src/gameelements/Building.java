package gameelements;

import exceptions.InsufficientResourcesException;
import exceptions.MaxLevelReachedException;

// Abstract base for all village buildings; handles level-cap logic and upgrade hooks.
public abstract class Building implements Updater {

    public static final int MAX_LEVEL = 5;

    protected double hitPoints;
    protected int level;
    protected double upgradeCostGold;
    protected double upgradeCostIron;
    protected double upgradeCostLumber;

    protected Building(double hitPoints, double upgradeCostGold,
                        double upgradeCostIron, double upgradeCostLumber) {
        this.hitPoints = hitPoints;
        this.level = 1;
        this.upgradeCostGold   = upgradeCostGold;
        this.upgradeCostIron   = upgradeCostIron;
        this.upgradeCostLumber = upgradeCostLumber;
    }

    public double getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(double hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getLevel() {
        return level;
    }

    public double getUpgradeCostGold() {
        return upgradeCostGold;
    }

    public double getUpgradeCostIron() {
        return upgradeCostIron;
    }

    public double getUpgradeCostLumber() {
        return upgradeCostLumber;
    }

    public void takeDamage(double amount) {
        this.hitPoints = Math.max(0, this.hitPoints - amount);
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    // Increments level, adds 50 base HP, then calls applyUpgradeBonus().
    // Resource deduction is handled by the caller (Village).
    @Override
    public void upgrade() throws MaxLevelReachedException, InsufficientResourcesException {
        if (level >= MAX_LEVEL) {
            throw new MaxLevelReachedException(getClass().getSimpleName(), MAX_LEVEL);
        }
        level++;
        hitPoints += 50; // base hp increase per level
        applyUpgradeBonus();
    }

    // Hook called after level increment; subclasses apply their stat improvements here.
    protected abstract void applyUpgradeBonus();

    public abstract String getName();

    @Override
    public String toString() {
        return String.format("%s[level=%d, hp=%.1f]", getName(), level, hitPoints);
    }
}
