package gameelements;

/**
 * Represents Gold, the primary currency in the village war game.
 * <p>
 * Gold is used to construct buildings, train most troop types, and perform upgrades.
 * It is produced by {@link GoldMine} buildings and {@link GoldMiner} peasants.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Gold extends Resource {

    /**
     * Constructs a Gold resource with the specified initial quantity.
     *
     * @param quantity the starting amount of gold
     */
    public Gold(double quantity) {
        super(quantity);
    }

    /**
     * Returns the resource display name.
     *
     * @return "Gold"
     */
    @Override
    public String getResourceName() {
        return "Gold";
    }
}
