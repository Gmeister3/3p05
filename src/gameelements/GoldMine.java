package gameelements;

// Produces gold per game tick; rate increases with each upgrade.
// Build cost: 100G / 50I / 50L
public class GoldMine extends Building {

    private double goldProd;

    public static final double COST_GOLD   = 100;
    public static final double COST_IRON   = 50;
    public static final double COST_LUMBER = 50;

    public GoldMine() {
        super(150, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.goldProd = 10.0;
    }

    public double getGoldProd() {
        return goldProd;
    }

    @Override
    protected void applyUpgradeBonus() {
        goldProd += 8.0;
    }

    @Override
    public String getName() {
        return "Gold Mine";
    }
}
