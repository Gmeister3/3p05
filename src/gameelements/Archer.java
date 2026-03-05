package gameelements;

// Ranged military unit; moderate damage, lower HP than melee.
// Train cost: 25G / 5I / 15L
public class Archer extends Fighter {

    public static final double COST_GOLD   = 25;
    public static final double COST_IRON   = 5;
    public static final double COST_LUMBER = 15;

    public Archer() {
        super("Archer", 18.0, 60.0);
    }
}
