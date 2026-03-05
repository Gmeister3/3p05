package gameelements;

// Heavily armoured cavalry unit; high damage and HP, expensive to train.
// Train cost: 50G / 30I / 10L
public class Knight extends Fighter {

    public static final double COST_GOLD   = 50;
    public static final double COST_IRON   = 30;
    public static final double COST_LUMBER = 10;

    public Knight() {
        super("Knight", 30.0, 150.0);
    }
}
