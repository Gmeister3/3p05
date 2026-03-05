package gameelements;

// Heavy defensive building; higher base damage than ArcherTower.
// Build cost: 100G / 80I / 40L
public class Cannon extends Building implements Damager {

    private double damage;

    public static final double COST_GOLD   = 100;
    public static final double COST_IRON   = 80;
    public static final double COST_LUMBER = 40;

    public Cannon() {
        super(300, COST_GOLD * 0.5, COST_IRON * 0.5, COST_LUMBER * 0.5);
        this.damage = 30.0;
    }

    @Override
    public double damage() {
        return damage;
    }

    @Override
    public void newOperation() {
        System.out.println(getName() + " is reloading and targeting.");
    }

    @Override
    protected void applyUpgradeBonus() {
        damage += 20.0;
    }

    @Override
    public String getName() {
        return "Cannon";
    }

    public double getDamage() {
        return damage;
    }
}
