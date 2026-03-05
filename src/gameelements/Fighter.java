package gameelements;

// Abstract base for military units; adds combat fields (damage, HP, level).
public abstract class Fighter extends Habitant {

    protected double damage;
    protected double hitPoints;
    protected int level;

    protected Fighter(String name, double damage, double hitPoints) {
        super(name);
        this.damage    = damage;
        this.hitPoints = hitPoints;
        this.level     = 1;
    }

    @Override
    public double damage() {
        return damage;
    }

    @Override
    public void newOperation() {
        System.out.println(name + " attacks for " + damage + " damage!");
    }

    public double getHitPoints() {
        return hitPoints;
    }

    public int getLevel() {
        return level;
    }

    public void takeDamage(double amount) {
        this.hitPoints = Math.max(0, this.hitPoints - amount);
    }

    public boolean isDefeated() {
        return hitPoints <= 0;
    }

    @Override
    public String toString() {
        return String.format("%s[level=%d, dmg=%.1f, hp=%.1f]",
                getClass().getSimpleName(), level, damage, hitPoints);
    }
}
