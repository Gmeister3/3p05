package gameelements;

// Supports village population; capacity increases with each upgrade.
// Build cost: 50G / 30I / 20L
public class Farm extends Building {

    private int population;

    public static final double COST_GOLD   = 50;
    public static final double COST_IRON   = 30;
    public static final double COST_LUMBER = 20;

    public Farm() {
        super(100, COST_GOLD * 0.4, COST_IRON * 0.4, COST_LUMBER * 0.4);
        this.population = 5;
    }

    public int getPopulation() {
        return population;
    }

    @Override
    protected void applyUpgradeBonus() {
        population += 3;
    }

    @Override
    public String getName() {
        return "Farm";
    }
}
