package gameelements;

/**
 * Abstract base class for all military units in the village army.
 * <p>
 * A {@code Fighter} extends {@link Habitant} and adds combat-specific fields:
 * damage output and hit points. All concrete fighter types ({@link Soldier},
 * {@link Archer}, {@link Knight}, {@link Catapult}) override the base values.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public abstract class Fighter extends Habitant {

    /** Damage this unit deals per attack. */
    protected double damage;

    /** Hit points of this unit; reaches 0 when the unit is defeated. */
    protected double hitPoints;

    /** Current level of this fighter (1–5). */
    protected int level;

    /**
     * Constructs a Fighter with specified combat statistics.
     *
     * @param name      display name of the fighter
     * @param damage    base damage per attack
     * @param hitPoints base hit points
     */
    protected Fighter(String name, double damage, double hitPoints) {
        super(name);
        this.damage    = damage;
        this.hitPoints = hitPoints;
        this.level     = 1;
    }

    /**
     * Returns the damage this fighter deals per attack.
     *
     * @return damage per attack
     */
    @Override
    public double damage() {
        return damage;
    }

    /**
     * Initiates a new attack operation for this fighter.
     */
    @Override
    public void newOperation() {
        System.out.println(name + " attacks for " + damage + " damage!");
    }

    /**
     * Returns the hit points of this fighter.
     *
     * @return current hit points
     */
    public double getHitPoints() {
        return hitPoints;
    }

    /**
     * Returns the current level of this fighter.
     *
     * @return fighter level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Applies damage to this fighter, reducing hit points.
     *
     * @param amount the damage amount to apply
     */
    public void takeDamage(double amount) {
        this.hitPoints = Math.max(0, this.hitPoints - amount);
    }

    /**
     * Returns whether this fighter has been eliminated.
     *
     * @return {@code true} if hit points are zero
     */
    public boolean isDefeated() {
        return hitPoints <= 0;
    }

    /**
     * Returns a string representation of this fighter.
     *
     * @return descriptive string with class, level, damage, and hp
     */
    @Override
    public String toString() {
        return String.format("%s[level=%d, dmg=%.1f, hp=%.1f]",
                getClass().getSimpleName(), level, damage, hitPoints);
    }
}
