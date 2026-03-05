package gameelements;

// Defensive building that fires arrows at attackers; damage increases per upgrade.
// Build cost: 60G / 40I / 30L
public class ArcherTower extends Building implements Damager {

    private double damage;

    public static final double COST_GOLD   = 60;
    public static final double COST_IRON   = 40;
    public static final double COST_LUMBER = 30;

    public ArcherTower() {
        super(200, COST_GOLD * 0.5, COST_IRON * 0.5, COST_LUMBER * 0.5);
        this.damage = 15.0;
    }

    @Override
    public double damage() {
        return damage;
    }

    @Override
    public void newOperation() {
        // Simulate resetting the firing cycle
        System.out.println(getName() + " is now targeting the enemy.");
    }

    @Override
    protected void applyUpgradeBonus() {
        damage += 10.0;
    }

    @Override
    public String getName() {
        return "Archer Tower";
    }

    public double getDamage() {
        return damage;
    }
}
