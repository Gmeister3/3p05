package gameelements;

// Central village building; its level caps the max upgrade level of all other buildings.
// Build cost: 200G / 100I / 100L (only one per village)
public class VillageHall extends Building {

    public static final double COST_GOLD   = 200;
    public static final double COST_IRON   = 100;
    public static final double COST_LUMBER = 100;

    public VillageHall() {
        super(500, COST_GOLD * 0.75, COST_IRON * 0.75, COST_LUMBER * 0.75);
    }

    // Returns the max level other buildings may reach (equals this hall's level).
    public int getMaxBuildingLevel() {
        return level;
    }

    @Override
    protected void applyUpgradeBonus() {
        hitPoints += 150; // VillageHall is sturdier than other buildings
    }

    @Override
    public String getName() {
        return "Village Hall";
    }
}
