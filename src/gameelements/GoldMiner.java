package gameelements;

// Worker that extracts gold; produces Gold resource each tick.
public class GoldMiner extends Peasant {

    public GoldMiner() {
        super("Gold Miner", 5.0);
    }

    @Override
    public Resource work() {
        return new Gold(getEffectiveProduction());
    }
}
