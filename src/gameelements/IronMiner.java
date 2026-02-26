package gameelements;

/**
 * An Iron Miner is a worker that smelts and collects iron ore.
 * <p>
 * Each game tick an IronMiner calls {@link #work()} and returns an {@link Iron}
 * resource proportional to their {@link #getEffectiveProduction()} rate.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class IronMiner extends Peasant {

    /**
     * Constructs an IronMiner at level 1 with a base production rate of 4 iron per tick.
     */
    public IronMiner() {
        super("Iron Miner", 4.0);
    }

    /**
     * Performs one work tick and produces iron.
     *
     * @return an {@link Iron} resource representing the iron mined this tick
     */
    @Override
    public Resource work() {
        return new Iron(getEffectiveProduction());
    }
}
