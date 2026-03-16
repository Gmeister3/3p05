package factory;

import gameelements.*;

/**
 * Factory (Creational Design Pattern) for creating {@link Building} instances.
 *
 * <p><b>Pattern: Factory</b></p>
 * <ul>
 *   <li><em>Client</em>: {@link controller.GameController} calls
 *       {@link #create(String)} whenever the player chooses to build a new structure.</li>
 *   <li><em>Factory</em>: this class – centralises and hides the {@code new} operator for
 *       every concrete building type, making it trivial to add new building types without
 *       touching the controller.</li>
 *   <li><em>Products</em>: {@link Farm}, {@link GoldMine}, {@link IronMine},
 *       {@link LumberMill}, {@link ArcherTower}, {@link Cannon}, {@link VillageHall}.</li>
 * </ul>
 */
public class BuildingFactory {

    /** Type constants – pass one of these to {@link #create(String)}. */
    public static final String FARM         = "Farm";
    public static final String GOLD_MINE    = "GoldMine";
    public static final String IRON_MINE    = "IronMine";
    public static final String LUMBER_MILL  = "LumberMill";
    public static final String ARCHER_TOWER = "ArcherTower";
    public static final String CANNON       = "Cannon";
    public static final String VILLAGE_HALL = "VillageHall";

    /**
     * Creates a new {@link Building} of the requested type.
     *
     * @param type one of the type-constant strings defined in this class
     * @return a freshly instantiated building
     * @throws IllegalArgumentException if the type is unrecognised
     */
    public Building create(String type) {
        switch (type) {
            case FARM:         return new Farm();
            case GOLD_MINE:    return new GoldMine();
            case IRON_MINE:    return new IronMine();
            case LUMBER_MILL:  return new LumberMill();
            case ARCHER_TOWER: return new ArcherTower();
            case CANNON:       return new Cannon();
            case VILLAGE_HALL: return new VillageHall();
            default:
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }

    /**
     * Returns the gold build-cost for the given building type.
     *
     * @param type building type constant
     * @return gold cost
     */
    public double getGoldCost(String type) {
        switch (type) {
            case FARM:         return Farm.COST_GOLD;
            case GOLD_MINE:    return GoldMine.COST_GOLD;
            case IRON_MINE:    return IronMine.COST_GOLD;
            case LUMBER_MILL:  return LumberMill.COST_GOLD;
            case ARCHER_TOWER: return ArcherTower.COST_GOLD;
            case CANNON:       return Cannon.COST_GOLD;
            case VILLAGE_HALL: return VillageHall.COST_GOLD;
            default:
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }

    /**
     * Returns the iron build-cost for the given building type.
     *
     * @param type building type constant
     * @return iron cost
     */
    public double getIronCost(String type) {
        switch (type) {
            case FARM:         return Farm.COST_IRON;
            case GOLD_MINE:    return GoldMine.COST_IRON;
            case IRON_MINE:    return IronMine.COST_IRON;
            case LUMBER_MILL:  return LumberMill.COST_IRON;
            case ARCHER_TOWER: return ArcherTower.COST_IRON;
            case CANNON:       return Cannon.COST_IRON;
            case VILLAGE_HALL: return VillageHall.COST_IRON;
            default:
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }

    /**
     * Returns the lumber build-cost for the given building type.
     *
     * @param type building type constant
     * @return lumber cost
     */
    public double getLumberCost(String type) {
        switch (type) {
            case FARM:         return Farm.COST_LUMBER;
            case GOLD_MINE:    return GoldMine.COST_LUMBER;
            case IRON_MINE:    return IronMine.COST_LUMBER;
            case LUMBER_MILL:  return LumberMill.COST_LUMBER;
            case ARCHER_TOWER: return ArcherTower.COST_LUMBER;
            case CANNON:       return Cannon.COST_LUMBER;
            case VILLAGE_HALL: return VillageHall.COST_LUMBER;
            default:
                throw new IllegalArgumentException("Unknown building type: " + type);
        }
    }
}
