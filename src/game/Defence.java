package game;

import gameelements.*;

import java.util.*;
import java.util.stream.*;

// Manages all defensive buildings in a village and calculates total defence output.
public class Defence {

    private final List<ArcherTower> archerTowers;
    private final List<Cannon> cannons;

    public Defence() {
        this.archerTowers = new ArrayList<>();
        this.cannons      = new ArrayList<>();
    }

    public void addArcherTower(ArcherTower tower) {
        archerTowers.add(tower);
    }

    public void addCannon(Cannon cannon) {
        cannons.add(cannon);
    }

    // Syncs defence with the village's building list using stream + instanceof filter.
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

    // Uses stream reduce to sum damage from all towers and cannons.
    public double getTotalDamage() {
        double towerDamage = archerTowers.stream()
                .mapToDouble(ArcherTower::damage)
                .reduce(0, Double::sum);
        double cannonDamage = cannons.stream()
                .mapToDouble(Cannon::damage)
                .reduce(0, Double::sum);
        return towerDamage + cannonDamage;
    }

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

    public int getArcherTowerCount() {
        return archerTowers.size();
    }

    public int getCannonCount() {
        return cannons.size();
    }

    @Override
    public String toString() {
        return String.format("Defence[towers=%d, cannons=%d, totalDmg=%.1f]",
                archerTowers.size(), cannons.size(), getTotalDamage());
    }
}
