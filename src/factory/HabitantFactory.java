package factory;

import gameelements.*;

/**
 * Factory (Creational Design Pattern) for creating {@link Habitant} instances.
 *
 * <p><b>Pattern: Factory</b></p>
 * <ul>
 *   <li><em>Client</em>: {@link controller.GameController} calls {@link #create(String)}
 *       whenever the player trains a new unit.</li>
 *   <li><em>Factory</em>: this class – centralises instantiation of every concrete habitant
 *       type; adding a new unit type only requires changing this class.</li>
 *   <li><em>Products</em>: {@link Soldier}, {@link Archer}, {@link Knight}, {@link Catapult}
 *       (fighters) and {@link GoldMiner}, {@link IronMiner}, {@link Lumberman}
 *       (workers/peasants).</li>
 * </ul>
 */
public class HabitantFactory {

    /** Type constants – pass one of these to {@link #create(String)}. */
    public static final String SOLDIER    = "Soldier";
    public static final String ARCHER     = "Archer";
    public static final String KNIGHT     = "Knight";
    public static final String CATAPULT   = "Catapult";
    public static final String GOLD_MINER = "GoldMiner";
    public static final String IRON_MINER = "IronMiner";
    public static final String LUMBERMAN  = "Lumberman";

    /**
     * Creates a new {@link Habitant} of the requested type.
     *
     * @param type one of the type-constant strings defined in this class
     * @return a freshly instantiated habitant
     * @throws IllegalArgumentException if the type is unrecognised
     */
    public Habitant create(String type) {
        switch (type) {
            case SOLDIER:    return new Soldier();
            case ARCHER:     return new Archer();
            case KNIGHT:     return new Knight();
            case CATAPULT:   return new Catapult();
            case GOLD_MINER: return new GoldMiner();
            case IRON_MINER: return new IronMiner();
            case LUMBERMAN:  return new Lumberman();
            default:
                throw new IllegalArgumentException("Unknown habitant type: " + type);
        }
    }

    /**
     * Returns the gold train-cost for the given habitant type, or 0 for free workers.
     *
     * @param type habitant type constant
     * @return gold cost
     */
    public double getGoldCost(String type) {
        switch (type) {
            case SOLDIER:    return Soldier.COST_GOLD;
            case ARCHER:     return Archer.COST_GOLD;
            case KNIGHT:     return Knight.COST_GOLD;
            case CATAPULT:   return Catapult.COST_GOLD;
            default:         return 0;
        }
    }

    /**
     * Returns the iron train-cost for the given habitant type, or 0 for free workers.
     *
     * @param type habitant type constant
     * @return iron cost
     */
    public double getIronCost(String type) {
        switch (type) {
            case SOLDIER:    return Soldier.COST_IRON;
            case ARCHER:     return Archer.COST_IRON;
            case KNIGHT:     return Knight.COST_IRON;
            case CATAPULT:   return Catapult.COST_IRON;
            default:         return 0;
        }
    }

    /**
     * Returns the lumber train-cost for the given habitant type, or 0 for free workers.
     *
     * @param type habitant type constant
     * @return lumber cost
     */
    public double getLumberCost(String type) {
        switch (type) {
            case SOLDIER:    return Soldier.COST_LUMBER;
            case ARCHER:     return Archer.COST_LUMBER;
            case KNIGHT:     return Knight.COST_LUMBER;
            case CATAPULT:   return Catapult.COST_LUMBER;
            default:         return 0;
        }
    }

    /**
     * Returns {@code true} if the given type is a fighter (military unit).
     *
     * @param type habitant type constant
     * @return whether the type is a fighter
     */
    public boolean isFighter(String type) {
        switch (type) {
            case SOLDIER: case ARCHER: case KNIGHT: case CATAPULT: return true;
            default: return false;
        }
    }
}
