package game;

import gameelements.*;
import exceptions.*;

import java.util.*;
import java.util.stream.*;

/**
 * Represents the player's (or an NPC's) village.
 * <p>
 * A Village contains:
 * <ul>
 *   <li>A list of {@link gameelements.Building}s (max {@value game.Village#MAX_BUILDINGS})</li>
 *   <li>A list of {@link gameelements.Habitant}s</li>
 *   <li>Three resource stores: {@link gameelements.Gold}, {@link gameelements.Iron},
 *       {@link gameelements.Lumber}</li>
 *   <li>A {@link gameelements.VillageHall} that constrains building upgrade levels</li>
 * </ul>
 * </p>
 * <p>
 * Demonstrates generics ({@code List<Building>}), anonymous comparators, local classes,
 * lambdas, and streams.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Village {

    /** Maximum number of buildings permitted in this village. */
    public static final int MAX_BUILDINGS = 20;

    /** Display name of this village. */
    private final String name;

    /** All buildings currently in this village. */
    private final List<Building> buildings;

    /** All inhabitants currently in this village. */
    private final List<Habitant> habitants;

    /** Current gold reserves. */
    private Gold gold;

    /** Current iron reserves. */
    private Iron iron;

    /** Current lumber reserves. */
    private Lumber lumber;

    /** Reference to the Village Hall (required for building level caps). */
    private VillageHall villageHall;

    /**
     * Constructs a new Village with the given name and starting resources.
     * One {@link Farm} and one {@link VillageHall} are added automatically.
     *
     * @param name the display name of this village
     */
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

    /**
     * Constructs a Village with custom starting resources (used for NPC villages).
     *
     * @param name       the display name
     * @param startGold  initial gold amount
     * @param startIron  initial iron amount
     * @param startLumber initial lumber amount
     */
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

    // -------------------------------------------------------------------------
    // Building management
    // -------------------------------------------------------------------------

    /**
     * Constructs and adds a new building to this village, deducting resource costs.
     *
     * @param building the {@link Building} to add
     * @throws BuildingLimitExceededException  if the village already has the maximum buildings
     * @throws InsufficientResourcesException  if the village lacks the required resources
     */
    public void build(Building building)
            throws BuildingLimitExceededException, InsufficientResourcesException {

        if (buildings.size() >= MAX_BUILDINGS) {
            throw new BuildingLimitExceededException(buildings.size(), MAX_BUILDINGS);
        }

        double costGold   = building.getUpgradeCostGold()   * 2; // build cost = 2× upgrade cost
        double costIron   = building.getUpgradeCostIron()   * 2;
        double costLumber = building.getUpgradeCostLumber() * 2;

        checkResources(costGold, costIron, costLumber);

        gold.subtract(costGold);
        iron.subtract(costIron);
        lumber.subtract(costLumber);

        buildings.add(building);
    }

    /**
     * Upgrades an existing building in this village, deducting upgrade costs.
     *
     * @param building the building to upgrade (must already be in this village)
     * @throws InvalidOperationException      if the building is not found in this village
     * @throws MaxLevelReachedException       if the building is at max level
     * @throws InsufficientResourcesException if resources are insufficient
     */
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

    // -------------------------------------------------------------------------
    // Habitant training
    // -------------------------------------------------------------------------

    /**
     * Trains and adds a new habitant (fighter or peasant) to the village.
     *
     * @param habitant    the {@link Habitant} to train
     * @param costGold    gold cost
     * @param costIron    iron cost
     * @param costLumber  lumber cost
     * @throws InsufficientResourcesException if the player cannot afford the unit
     */
    public void train(Habitant habitant, double costGold, double costIron, double costLumber)
            throws InsufficientResourcesException {

        checkResources(costGold, costIron, costLumber);

        gold.subtract(costGold);
        iron.subtract(costIron);
        lumber.subtract(costLumber);

        habitants.add(habitant);
    }

    // -------------------------------------------------------------------------
    // Score calculations
    // -------------------------------------------------------------------------

    /**
     * Calculates the village's defence score.
     * <p>
     * Uses a lambda-based stream reduction over all buildings:
     * {@code defenceScore = sum of (level × hitPoints) for each building}.
     * </p>
     *
     * @return the total defence score as a double
     */
    public double getDefenceScore() {
        // Lambda + stream to compute defence score
        return buildings.stream()
                .mapToDouble(b -> b.getLevel() * b.getHitPoints())
                .sum();
    }

    /**
     * Calculates the village's offensive score from its habitant fighters.
     * <p>
     * Uses a stream with an instanceof filter and method reference:
     * {@code attackScore = sum of (damage × level) for each Fighter}.
     * </p>
     *
     * @return the total attack score as a double
     */
    public double getAttackScore() {
        // Stream with filter and lambda for attack score
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .mapToDouble(f -> f.damage() * f.getLevel())
                .sum();
    }

    // -------------------------------------------------------------------------
    // Resource helpers
    // -------------------------------------------------------------------------

    /**
     * Verifies that the village has enough of each resource; throws if not.
     *
     * @param needGold   required gold
     * @param needIron   required iron
     * @param needLumber required lumber
     * @throws InsufficientResourcesException if any resource is short
     */
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

    /**
     * Adds loot to the village's resource stores (used after a successful attack).
     *
     * @param g  gold to add
     * @param i  iron to add
     * @param l  lumber to add
     */
    public void addResources(double g, double i, double l) {
        gold.add(g);
        iron.add(i);
        lumber.add(l);
    }

    // -------------------------------------------------------------------------
    // Sorted views using anonymous Comparator classes
    // -------------------------------------------------------------------------

    /**
     * Returns buildings sorted by level in descending order.
     * <p>
     * Uses an anonymous {@link Comparator} class.
     * </p>
     *
     * @return sorted list of buildings (highest level first)
     */
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

    /**
     * Returns fighters sorted by damage in descending order using a lambda comparator.
     *
     * @return sorted list of fighters (highest damage first)
     */
    public List<Fighter> getFightersSortedByDamage() {
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .sorted(Comparator.comparingDouble(Fighter::damage).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Returns an unmodifiable view of the building list.
     *
     * @return unmodifiable list of buildings
     */
    public List<Building> getBuildings() {
        return Collections.unmodifiableList(buildings);
    }

    /**
     * Returns a mutable reference to the internal building list (for NPC village setup).
     *
     * @return mutable building list
     */
    public List<Building> getBuildingsMutable() {
        return buildings;
    }

    /**
     * Returns an unmodifiable view of the habitant list.
     *
     * @return unmodifiable list of habitants
     */
    public List<Habitant> getHabitants() {
        return Collections.unmodifiableList(habitants);
    }

    /**
     * Returns a mutable reference to the internal habitant list.
     *
     * @return mutable habitant list
     */
    public List<Habitant> getHabitantsMutable() {
        return habitants;
    }

    /**
     * Returns all fighters (combat units) in this village.
     * <p>Uses a stream with {@code instanceof} filter.</p>
     *
     * @return list of {@link Fighter} instances
     */
    public List<Fighter> getFighters() {
        return habitants.stream()
                .filter(h -> h instanceof Fighter)
                .map(h -> (Fighter) h)
                .collect(Collectors.toList());
    }

    /**
     * Returns all peasants (worker units) in this village.
     *
     * @return list of {@link Peasant} instances
     */
    public List<Peasant> getPeasants() {
        return habitants.stream()
                .filter(h -> h instanceof Peasant)
                .map(h -> (Peasant) h)
                .collect(Collectors.toList());
    }

    /**
     * Returns the gold resource store.
     *
     * @return the {@link Gold} resource
     */
    public Gold getGold() { return gold; }

    /**
     * Returns the iron resource store.
     *
     * @return the {@link Iron} resource
     */
    public Iron getIron() { return iron; }

    /**
     * Returns the lumber resource store.
     *
     * @return the {@link Lumber} resource
     */
    public Lumber getLumber() { return lumber; }

    /**
     * Returns the Village Hall of this village.
     *
     * @return the {@link VillageHall}
     */
    public VillageHall getVillageHall() { return villageHall; }

    /**
     * Returns the display name of this village.
     *
     * @return village name
     */
    public String getName() { return name; }

    /**
     * Returns a detailed status summary for display purposes.
     *
     * @return formatted multi-line status string
     */
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
