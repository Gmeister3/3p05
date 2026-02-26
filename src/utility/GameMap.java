package utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the overworld game map that contains all villages and their positions.
 * <p>
 * The {@code GameMap} holds a {@link Region} that defines the total playable area
 * and maintains a mapping from village names to their {@link Position} on the map.
 * It uses a {@link java.util.Map} to store village locations efficiently.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class GameMap {

    /** The region representing the total playable area of the map. */
    private final Region mapRegion;

    /**
     * Internal map from village name to map position.
     * Demonstrates use of {@link java.util.Map}&lt;K,V&gt;.
     */
    private final Map<String, Position> villageLocations;

    /**
     * Constructs a GameMap with the specified map region.
     *
     * @param mapRegion the {@link Region} that defines the full map bounds
     */
    public GameMap(Region mapRegion) {
        this.mapRegion = mapRegion;
        this.villageLocations = new HashMap<>();
    }

    /**
     * Returns the region that defines the bounds of this map.
     *
     * @return the {@link Region} of the map
     */
    public Region getMapRegion() {
        return mapRegion;
    }

    /**
     * Registers a village and its position on the map.
     *
     * @param villageName the unique name of the village
     * @param position    the position of the village on the map
     */
    public void addVillage(String villageName, Position position) {
        villageLocations.put(villageName, position);
    }

    /**
     * Removes a village from the map registry.
     *
     * @param villageName the name of the village to remove
     */
    public void removeVillage(String villageName) {
        villageLocations.remove(villageName);
    }

    /**
     * Returns the position of a village on the map.
     *
     * @param villageName the name of the village
     * @return the {@link Position} of the village, or {@code null} if not found
     */
    public Position getVillagePosition(String villageName) {
        return villageLocations.get(villageName);
    }

    /**
     * Returns an unmodifiable view of all village locations.
     *
     * @return an unmodifiable {@link Map} of village names to positions
     */
    public Map<String, Position> getAllVillageLocations() {
        return java.util.Collections.unmodifiableMap(villageLocations);
    }

    /**
     * Checks whether a position is within the valid map bounds.
     *
     * @param position the position to validate
     * @return {@code true} if the position is within the map region
     */
    public boolean isValidPosition(Position position) {
        return mapRegion.contains(position);
    }

    /**
     * Returns a string representation of the game map.
     *
     * @return string describing the map region and number of registered villages
     */
    @Override
    public String toString() {
        return "GameMap{region=" + mapRegion + ", villages=" + villageLocations.size() + "}";
    }
}
