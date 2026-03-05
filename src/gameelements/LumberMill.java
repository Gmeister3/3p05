package gameelements;

// Produces lumber per game tick; rate increases with each upgrade.
// Build cost: 30G / 10I / 60L
public class LumberMill extends Building {

    private double lumbProd;

    public static final double COST_GOLD   = 30;
    public static final double COST_IRON   = 10;
    public static final double COST_LUMBER = 60;

    public LumberMill() {
        super(120, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.lumbProd = 12.0;
    }

    public double getLumbProd() {
        return lumbProd;
    }

    @Override
    protected void applyUpgradeBonus() {
        lumbProd += 7.0;
    }

    @Override
    public String getName() {
        return "Lumber Mill";
    }
}
