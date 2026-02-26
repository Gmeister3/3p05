package gameelements;

/**
 * A Gold Miner is a worker that extracts gold from mines.
 * <p>
 * Each game tick a GoldMiner calls {@link #work()} and returns a {@link Gold}
 * resource proportional to their {@link #getEffectiveProduction()} rate.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class GoldMiner extends Peasant {

    /**
     * Constructs a GoldMiner at level 1 with a base production rate of 5 gold per tick.
     */
    public GoldMiner() {
        super("Gold Miner", 5.0);
    }

    /**
     * Performs one work tick and produces gold.
     *
     * @return a {@link Gold} resource representing the gold mined this tick
     */
    @Override
    public Resource work() {
        return new Gold(getEffectiveProduction());
    }
}
