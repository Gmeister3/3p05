package gameelements;

// Siege engine with highest damage but very low HP.
// Train cost: 80G / 60I / 50L
public class Catapult extends Fighter {

    public static final double COST_GOLD   = 80;
    public static final double COST_IRON   = 60;
    public static final double COST_LUMBER = 50;

    public Catapult() {
        super("Catapult", 60.0, 40.0);
    }
}
