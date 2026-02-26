package game;

import gameelements.*;

import java.util.*;
import java.util.stream.*;

/**
 * Manages all defensive buildings in a village and calculates total defence output.
 * <p>
 * The {@code Defence} class aggregates {@link ArcherTower} and {@link Cannon} instances
 * and exposes utility methods that use streams and wildcards to compute total damage.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Defence {

    /** List of archer towers in this defence. */
    private final List<ArcherTower> archerTowers;

    /** List of cannons in this defence. */
    private final List<Cannon> cannons;

    /**
     * Constructs an empty Defence configuration.
     */
    public Defence() {
        this.archerTowers = new ArrayList<>();
        this.cannons      = new ArrayList<>();
    }

    /**
     * Registers an {@link ArcherTower} with the defence.
     *
     * @param tower the archer tower to add
     */
    public void addArcherTower(ArcherTower tower) {
        archerTowers.add(tower);
    }

    /**
     * Registers a {@link Cannon} with the defence.
     *
     * @param cannon the cannon to add
     */
    public void addCannon(Cannon cannon) {
        cannons.add(cannon);
    }

    /**
     * Synchronises this Defence with all defensive buildings in the given village.
     * <p>
     * Uses a stream + {@code instanceof} filter to find relevant buildings.
     * Demonstrates {@code List<? extends Building>} wildcard usage.
     * </p>
     *
     * @param buildings the village's building list (upper-bounded wildcard)
     */
    public void syncWithVillage(List<? extends Building> buildings) {
        archerTowers.clear();
        cannons.clear();
        buildings.stream()
                 .filter(b -> b instanceof ArcherTower)
                 .map(b -> (ArcherTower) b)
                 .forEach(archerTowers::add);
        buildings.stream()
                 .filter(b -> b instanceof Cannon)
                 .map(b -> (Cannon) b)
                 .forEach(cannons::add);
    }

    /**
     * Calculates the total damage output of all defensive structures.
     * <p>Uses a stream {@code reduce} operation.</p>
     *
     * @return total defence damage per tick
     */
    public double getTotalDamage() {
        double towerDamage = archerTowers.stream()
                .mapToDouble(ArcherTower::damage)
                .reduce(0, Double::sum);
        double cannonDamage = cannons.stream()
                .mapToDouble(Cannon::damage)
                .reduce(0, Double::sum);
        return towerDamage + cannonDamage;
    }

    /**
     * Returns all defensive buildings as a combined list.
     * <p>Demonstrates use of a local class for list merging.</p>
     *
     * @return combined list of all defensive buildings
     */
    public List<Building> getAllDefenceBuildings() {

        // Local class that merges two building lists
        class BuildingMerger {
            List<Building> merge(List<? extends Building> a, List<? extends Building> b) {
                List<Building> result = new ArrayList<>(a);
                result.addAll(b);
                return result;
            }
        }

        BuildingMerger merger = new BuildingMerger();
        return merger.merge(archerTowers, cannons);
    }

    /**
     * Returns the number of archer towers.
     *
     * @return archer tower count
     */
    public int getArcherTowerCount() {
        return archerTowers.size();
    }

    /**
     * Returns the number of cannons.
     *
     * @return cannon count
     */
    public int getCannonCount() {
        return cannons.size();
    }

    /**
     * Returns a string summary of the defence configuration.
     *
     * @return formatted defence summary
     */
    @Override
    public String toString() {
        return String.format("Defence[towers=%d, cannons=%d, totalDmg=%.1f]",
                archerTowers.size(), cannons.size(), getTotalDamage());
    }
}
