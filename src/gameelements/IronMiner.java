package gameelements;

// Worker that collects iron; produces Iron resource each tick.
public class IronMiner extends Peasant {

    public IronMiner() {
        super("Iron Miner", 4.0);
    }

    @Override
    public Resource work() {
        return new Iron(getEffectiveProduction());
    }
}
