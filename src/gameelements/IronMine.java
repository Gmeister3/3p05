package gameelements;

// Produces iron per game tick; rate increases with each upgrade.
// Build cost: 80G / 60I / 40L
public class IronMine extends Building {

    private double ironProd;

    public static final double COST_GOLD   = 80;
    public static final double COST_IRON   = 60;
    public static final double COST_LUMBER = 40;

    public IronMine() {
        super(150, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.ironProd = 8.0;
    }

    public double getIronProd() {
        return ironProd;
    }

    @Override
    protected void applyUpgradeBonus() {
        ironProd += 6.0;
    }

    @Override
    public String getName() {
        return "Iron Mine";
    }
}
