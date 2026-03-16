package game;

import gameelements.*;
import exceptions.*;

import java.util.*;
import java.util.stream.*;

// Represents a player's or NPC village containing buildings, habitants, and resources.
public class Village {

    public static final int MAX_BUILDINGS = 20;

    private final String name;
    private final List<Building> buildings;
    private final List<Habitant> habitants;
    private Gold gold;
    private Iron iron;
    private Lumber lumber;
    private VillageHall villageHall;

    // Starts with a VillageHall and a Farm; default resources: 500G / 300I / 400L.
    public Village(String name) {
        this.name      = name;
        this.buildings = new ArrayList<>();
        this.habitants = new ArrayList<>();
        this.gold      = new Gold(500);
        this.iron      = new Iron(300);
        this.lumber    = new Lumber(400);

        // Every village starts with a VillageHall and a Farm
        this.villageHall = new VillageHall();
        buildings.add(villageHall);
        buildings.add(new Farm());
    }

    // Constructor for NPC villages with custom starting resources.
    public Village(String name, double startGold, double startIron, double startLumber) {
        this.name      = name;
        this.buildings = new ArrayList<>();
        this.habitants = new ArrayList<>();
        this.gold      = new Gold(startGold);
        this.iron      = new Iron(startIron);
        this.lumber    = new Lumber(startLumber);

        this.villageHall = new VillageHall();
        buildings.add(villageHall);
        buildings.add(new Farm());
    }

    // Adds a building to the village, deducting build costs (2x upgrade cost).
    public void build(Building building)
            throws BuildingLimitExceededException, InsufficientResourcesException {

        if (buildings.size() >= MAX_BUILDINGS) {
            throw new BuildingLimitExceededException(buildings.size(), MAX_BUILDINGS);
        }

        double costGold   = building.getUpgradeCostGold()   * 2; // build cost = 2x upgrade cost
        double costIron   = building.getUpgradeCostIron()   * 2;
        double costLumber = building.getUpgradeCostLumber() * 2;

        checkResources(costGold, costIron, costLumber);

        gold.subtract(costGold);
        iron.subtract(costIron);
        lumber.subtract(costLumber);

        buildings.add(building);
    }

    // Upgrades a building, enforcing the VillageHall level cap.
    public void upgrade(Building building)
            throws InvalidOperationException, MaxLevelReachedException,
                   InsufficientResourcesException {

        if (!buildings.contains(building)) {
            throw new InvalidOperationException("upgrade", "Building not found in this village.");
        }

        // Enforce Village Hall level cap
        if (!(building instanceof VillageHall) && building.getLevel() >= villageHall.getLevel()) {
            throw new MaxLevelReachedException(building.getName(),
                    villageHall.getLevel());
        }

        checkResources(building.getUpgradeCostGold(),
                       building.getUpgradeCostIron(),
                       building.getUpgradeCostLumber());

        gold.subtract(building.getUpgradeCostGold());
        iron.subtract(building.getUpgradeCostIron());
        lumber.subtract(building.getUpgradeCostLumber());

        building.upgrade();
    }

    public void train(Habitant habitant, double costGold, double costIron, double costLumber)
            throws InsufficientResourcesException {

        checkResources(costGold, costIron, costLumber);

        gold.subtract(costGold);
        iron.subtract(costIron);
        lumber.subtract(costLumber);

        habitants.add(habitant);
    }

    // Defence score = sum of (level x hitPoints) for each building.
    public double getDefenceScore() {
        // Lambda + stream to compute defence score
        return buildings.stream()
                .mapToDouble(b -> b.getLevel() * b.getHitPoints())
                .sum();
    }

    // Attack score = sum of (damage x level) for each Fighter habitant.
    public double getAttackScore() {
        // Stream with filter and lambda for attack score
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .mapToDouble(f -> f.damage() * f.getLevel())
                .sum();
    }

    // Throws InsufficientResourcesException if any resource is below the required amount.
    private void checkResources(double needGold, double needIron, double needLumber)
            throws InsufficientResourcesException {

        if (!gold.hasEnough(needGold)) {
            throw new InsufficientResourcesException("Gold", needGold, gold.getQuantity());
        }
        if (!iron.hasEnough(needIron)) {
            throw new InsufficientResourcesException("Iron", needIron, iron.getQuantity());
        }
        if (!lumber.hasEnough(needLumber)) {
            throw new InsufficientResourcesException("Lumber", needLumber, lumber.getQuantity());
        }
    }

    public void addResources(double g, double i, double l) {
        gold.add(g);
        iron.add(i);
        lumber.add(l);
    }

    // Returns buildings sorted by level descending using an anonymous Comparator.
    public List<Building> getBuildingsSortedByLevel() {
        List<Building> sorted = new ArrayList<>(buildings);
        // Anonymous Comparator class
        Collections.sort(sorted, new Comparator<Building>() {
            @Override
            public int compare(Building a, Building b) {
                return Integer.compare(b.getLevel(), a.getLevel());
            }
        });
        return sorted;
    }

    public List<Fighter> getFightersSortedByDamage() {
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .sorted(Comparator.comparingDouble(Fighter::damage).reversed())
                .collect(Collectors.toList());
    }

    public List<Building> getBuildings() {
        return Collections.unmodifiableList(buildings);
    }

    public List<Building> getBuildingsMutable() {
        return buildings;
    }

    public List<Habitant> getHabitants() {
        return Collections.unmodifiableList(habitants);
    }

    public List<Habitant> getHabitantsMutable() {
        return habitants;
    }

    public List<Fighter> getFighters() {
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .collect(Collectors.toList());
    }

    public List<Peasant> getPeasants() {
        return habitants.stream()
                .filter(h -> h instanceof Peasant)
                .map(h -> (Peasant) h)
                .collect(Collectors.toList());
    }

    public Gold getGold() { return gold; }
    public Iron getIron() { return iron; }
    public Lumber getLumber() { return lumber; }
    public VillageHall getVillageHall() { return villageHall; }

    /**
     * Replaces the tracked VillageHall reference.
     * Used by {@link persistence.VillageSerializer} when loading a saved village so that
     * the upgrade-level cap reflects the loaded hall rather than the constructor's default.
     *
     * @param villageHall the VillageHall instance added to the buildings list
     */
    public void setVillageHall(VillageHall villageHall) { this.villageHall = villageHall; }

    public String getName() { return name; }

    public String getStatusSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Village: ").append(name).append(" ===\n");
        sb.append(String.format("  Gold: %.1f | Iron: %.1f | Lumber: %.1f%n",
                gold.getQuantity(), iron.getQuantity(), lumber.getQuantity()));
        sb.append(String.format("  Defence Score: %.1f | Attack Score: %.1f%n",
                getDefenceScore(), getAttackScore()));
        sb.append("  Buildings (").append(buildings.size()).append("/").append(MAX_BUILDINGS).append("):\n");

        // Use lambda forEach
        buildings.forEach(b -> sb.append("    - ").append(b).append("\n"));

        sb.append("  Habitants (").append(habitants.size()).append("):\n");
        habitants.forEach(h -> sb.append("    - ").append(h).append("\n"));

        return sb.toString();
    }
}
