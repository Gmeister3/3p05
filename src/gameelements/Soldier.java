package gameelements;

// Basic melee infantry; cheapest military unit.
// Train cost: 20G / 10I / 5L
public class Soldier extends Fighter {

    public static final double COST_GOLD   = 20;
    public static final double COST_IRON   = 10;
    public static final double COST_LUMBER = 5;

    public Soldier() {
        super("Soldier", 12.0, 80.0);
    }
}
