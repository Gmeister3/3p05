package gameelements;

// Worker that chops lumber; produces Lumber resource each tick.
public class Lumberman extends Peasant {

    public Lumberman() {
        super("Lumberman", 6.0);
    }

    @Override
    public Resource work() {
        return new Lumber(getEffectiveProduction());
    }
}
