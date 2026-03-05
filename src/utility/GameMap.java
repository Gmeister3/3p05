package utility;

import java.util.HashMap;
import java.util.Map;

// Overworld map holding all village positions within a bounded Region.
public class GameMap {

    private final Region mapRegion;
    // maps village name to map position
    private final Map<String, Position> villageLocations;

    public GameMap(Region mapRegion) {
        this.mapRegion = mapRegion;
        this.villageLocations = new HashMap<>();
    }

    public Region getMapRegion() {
        return mapRegion;
    }

    public void addVillage(String villageName, Position position) {
        villageLocations.put(villageName, position);
    }

    public void removeVillage(String villageName) {
        villageLocations.remove(villageName);
    }

    public Position getVillagePosition(String villageName) {
        return villageLocations.get(villageName);
    }

    public Map<String, Position> getAllVillageLocations() {
        return java.util.Collections.unmodifiableMap(villageLocations);
    }

    public boolean isValidPosition(Position position) {
        return mapRegion.contains(position);
    }

    @Override
    public String toString() {
        return "GameMap{region=" + mapRegion + ", villages=" + villageLocations.size() + "}";
    }
}
