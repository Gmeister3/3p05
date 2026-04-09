package controller;

import exceptions.*;
import factory.BuildingFactory;
import factory.HabitantFactory;
import game.*;
import gameelements.*;
import gui.GraphicalInterface;
import persistence.VillageSerializer;
import utility.AttackOutcome;

import java.io.IOException;
import java.util.*;

// Controller layer of the MVC (Model-View-Controller) architectural pattern.
// Pattern: MVC – Controller
//   Model: GameEngine, Player, Village, Army, and all classes in game/ and gameelements/.
//   View: GraphicalInterface – renders state to the console without containing any game
//     logic.
//   Controller: this class – receives user input (forwarded from Main), interprets
//     commands, mutates the Model via its own API, and tells the View to refresh.
//
// The controller also uses the BuildingFactory and HabitantFactory (Factory pattern)
// to create new game objects transparently.
public class GameController {

    /* ------------------------------------------------------------------ */
    /*  MVC collaborators                                                  */
    /* ------------------------------------------------------------------ */

    /** Model – central game state. */
    private final GameEngine engine;

    /** Model – the human player. */
    private final Player player;

    /** View – all console rendering. */
    private final GraphicalInterface view;

    /** Model – resource harvesting helper. */
    private final CollectResources collector;

    /** Factory for building objects (Factory pattern). */
    private final BuildingFactory buildingFactory;

    /** Factory for habitant objects (Factory pattern). */
    private final HabitantFactory habitantFactory;

    /** Scanner forwarded from Main to keep I/O ownership in one place. */
    private final Scanner scanner;

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Constructs the controller wiring the model, view, and I/O together.
     *
     * @param engine   the {@link GameEngine} (model)
     * @param player   the human {@link Player} (model)
     * @param view     the {@link GraphicalInterface} (view)
     * @param scanner  console input stream from Main
     */
    public GameController(GameEngine engine, Player player,
                          GraphicalInterface view, Scanner scanner) {
        this.engine          = engine;
        this.player          = player;
        this.view            = view;
        this.scanner         = scanner;
        this.collector       = new CollectResources();
        this.buildingFactory = new BuildingFactory();
        this.habitantFactory = new HabitantFactory();
    }

    /* ------------------------------------------------------------------ */
    /*  Main dispatch                                                      */
    /* ------------------------------------------------------------------ */

    /**
     * Dispatches a main-menu choice to the appropriate handler.
     *
     * @param choice the single-character string entered by the player
     * @throws InsufficientResourcesException  if the player cannot afford an action
     * @throws MaxLevelReachedException        if a building is already at maximum level
     * @throws BuildingLimitExceededException  if the village is full
     * @throws InvalidOperationException       if the operation is not currently valid
     */
    public void handleMenuChoice(String choice)
            throws InsufficientResourcesException, MaxLevelReachedException,
                   BuildingLimitExceededException, InvalidOperationException {

        switch (choice) {
            case "1": viewVillage();      break;
            case "2": buildBuilding();    break;
            case "3": trainUnit();        break;
            case "4": upgradeBuilding();  break;
            case "5": exploreTargets();   break;
            case "6": attackVillage();    break;
            case "7": collectResources(); break;
            case "8": viewRanking();      break;
            case "9": viewArmy();         break;
            case "s": saveVillage();      break;
            case "l": loadVillage();      break;
            case "0": engine.setRunning(false); break;
            default:
                System.out.println("  Invalid choice. Please enter 0-9 (or s=save, l=load).");
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Menu handlers                                                      */
    /* ------------------------------------------------------------------ */

    /** Delegates village rendering to the View. */
    private void viewVillage() {
        view.renderVillage(player.getVillage());
    }

    /**
     * Builds a new building using the {@link BuildingFactory}.
     * The factory centralises instantiation so the controller does not need to know
     * the concrete class – only the type string.
     */
    private void buildBuilding()
            throws BuildingLimitExceededException, InsufficientResourcesException {

        view.renderBuildMenu();
        String choice = scanner.nextLine().trim();
        System.out.println();

        String type = mapBuildChoice(choice);
        if (type == null) {
            System.out.println("  Cancelled / invalid choice.");
            return;
        }

        double costGold   = buildingFactory.getGoldCost(type);
        double costIron   = buildingFactory.getIronCost(type);
        double costLumber = buildingFactory.getLumberCost(type);

        Village village = player.getVillage();

        if (!village.getGold().hasEnough(costGold)) {
            throw new InsufficientResourcesException("Gold", costGold,
                    village.getGold().getQuantity());
        }
        if (!village.getIron().hasEnough(costIron)) {
            throw new InsufficientResourcesException("Iron", costIron,
                    village.getIron().getQuantity());
        }
        if (!village.getLumber().hasEnough(costLumber)) {
            throw new InsufficientResourcesException("Lumber", costLumber,
                    village.getLumber().getQuantity());
        }
        if (village.getBuildings().size() >= Village.MAX_BUILDINGS) {
            throw new BuildingLimitExceededException(
                    village.getBuildings().size(), Village.MAX_BUILDINGS);
        }

        // Use factory to instantiate the building (Factory pattern)
        Building building = buildingFactory.create(type);

        village.getGold().subtract(costGold);
        village.getIron().subtract(costIron);
        village.getLumber().subtract(costLumber);
        village.getBuildingsMutable().add(building);

        System.out.println("  Built: " + building.getName());
        player.recordAttack(10, true);
    }

    /**
     * Trains a new habitant using the {@link HabitantFactory}.
     * The factory hides the concrete class so the controller only needs the type string
     * to create any fighter or worker.
     */
    private void trainUnit() throws InsufficientResourcesException {

        view.renderTrainMenu();
        String choice = scanner.nextLine().trim();
        System.out.println();

        String type = mapTrainChoice(choice);
        if (type == null) {
            System.out.println("  Cancelled / invalid choice.");
            return;
        }

        double cg = habitantFactory.getGoldCost(type);
        double ci = habitantFactory.getIronCost(type);
        double cl = habitantFactory.getLumberCost(type);

        // Use factory to instantiate the habitant (Factory pattern)
        Habitant habitant = habitantFactory.create(type);
        player.getVillage().train(habitant, cg, ci, cl);
        System.out.println("  Trained: " + type);

        if (habitantFactory.isFighter(type)) {
            System.out.print("  Add to army? (y/n): ");
            String add = scanner.nextLine().trim();
            if ("y".equalsIgnoreCase(add)) {
                try {
                    player.getArmy().addFighter((Fighter) habitant);
                    System.out.println("  Fighter added to army.");
                } catch (InvalidOperationException e) {
                    System.out.println("  [!] " + e.getMessage());
                }
            }
        }
    }

    /** Upgrades a building chosen by the player. */
    private void upgradeBuilding()
            throws MaxLevelReachedException, InsufficientResourcesException,
                   InvalidOperationException {

        List<Building> buildings = new ArrayList<>(player.getVillage().getBuildings());
        if (buildings.isEmpty()) {
            System.out.println("  No buildings to upgrade.");
            return;
        }

        view.renderUpgradeMenu(buildings, player.getVillage().getVillageHall().getLevel());

        String input = scanner.nextLine().trim();
        System.out.println();

        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input.");
            return;
        }

        if (idx < 0) { System.out.println("  Cancelled."); return; }
        if (idx >= buildings.size()) { System.out.println("  Invalid choice."); return; }

        Building chosen = buildings.get(idx);
        player.getVillage().upgrade(chosen);
        System.out.println("  Upgraded " + chosen.getName() + " to level " + chosen.getLevel());
        player.recordAttack(20, true);
    }

    /** Lists available NPC targets via the View. */
    private void exploreTargets() {
        List<Village> targets = engine.getAvailableTargets();
        view.renderTargets(targets);
    }

    /** Executes a player attack on a chosen NPC village. */
    private void attackVillage() throws InvalidOperationException {
        if (player.getArmy().isEmpty()) {
            throw new InvalidOperationException("attack",
                    "Your army is empty. Train fighters first.");
        }

        List<Village> targets = engine.getAvailableTargets();
        if (targets.isEmpty()) {
            System.out.println("  No targets available.");
            return;
        }

        view.renderTargets(targets);
        System.out.print("  Select target (1-" + targets.size() + ", 0=cancel): ");
        String input = scanner.nextLine().trim();
        System.out.println();

        int idx;
        try {
            idx = Integer.parseInt(input) - 1;
        } catch (NumberFormatException e) {
            System.out.println("  Invalid input.");
            return;
        }

        if (idx < 0) { System.out.println("  Cancelled."); return; }
        if (idx >= targets.size()) { System.out.println("  Invalid choice."); return; }

        Village target = targets.get(idx);

        System.out.println("  Attacking '" + target.getName() + "'...");
        System.out.printf("  Your attack score: %.1f | Their defence: %.1f%n",
                player.getArmy().getAttackScore(), target.getDefenceScore());

        AttackOutcome outcome = player.getArmy().attack(target);
        System.out.println("  " + outcome.getMessage());

        if (outcome.isSuccess()) {
            player.getVillage().addResources(
                    outcome.getNewGold(), outcome.getNewIron(), outcome.getNewLumber());
            int points = (int) (outcome.getNewGold() + outcome.getNewIron() + outcome.getNewLumber());
            player.recordAttack(points + 50, true);
            System.out.printf("  Resources gained: +%.1f Gold, +%.1f Iron, +%.1f Lumber%n",
                    outcome.getNewGold(), outcome.getNewIron(), outcome.getNewLumber());
            player.notify(target);
        } else {
            player.recordAttack(0, false);
        }
    }

    /** Collects resources via workers and production buildings. */
    private void collectResources() {
        String result = collector.collect(player.getVillage());
        System.out.println("  " + result);
    }

    /** Shows the leaderboard and player ranking via the View. */
    private void viewRanking() {
        engine.printLeaderboard();
        System.out.println("  " + player.getRankingSummary());
        System.out.printf("  Village defence score : %.1f%n",
                player.getVillage().getDefenceScore());
        System.out.printf("  Army attack score     : %.1f%n",
                player.getArmy().getAttackScore());
    }

    /** Shows the army composition via the View. */
    private void viewArmy() {
        view.renderArmy(player.getArmy());
    }

    /* ------------------------------------------------------------------ */
    /*  XML / JSON persistence (bonus)                                     */
    /* ------------------------------------------------------------------ */

    /**
     * Saves the player's village state to {@code village_save.xml}.
     * Demonstrates the XML persistence bonus requirement.
     */
    private void saveVillage() {
        try {
            VillageSerializer.save(player.getVillage(), "village_save.xml");
            System.out.println("  Village saved to village_save.xml");
        } catch (IOException e) {
            System.out.println("  [!] Failed to save village: " + e.getMessage());
        }
    }

    /**
     * Loads a village state from {@code village_save.xml} and replaces the current village.
     * Demonstrates the XML persistence bonus requirement.
     */
    private void loadVillage() {
        try {
            Village loaded = VillageSerializer.load("village_save.xml");
            player.setVillage(loaded);
            System.out.println("  Village loaded from village_save.xml: " + loaded.getName());
            view.renderVillage(loaded);
        } catch (IOException | RuntimeException e) {
            System.out.println("  [!] Failed to load village: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Helper mapping methods                                             */
    /* ------------------------------------------------------------------ */

    /**
     * Maps a build-menu digit to a {@link BuildingFactory} type constant.
     *
     * @param choice single-character input
     * @return factory type constant, or {@code null} for cancel/invalid
     */
    private String mapBuildChoice(String choice) {
        switch (choice) {
            case "1": return BuildingFactory.FARM;
            case "2": return BuildingFactory.GOLD_MINE;
            case "3": return BuildingFactory.IRON_MINE;
            case "4": return BuildingFactory.LUMBER_MILL;
            case "5": return BuildingFactory.ARCHER_TOWER;
            case "6": return BuildingFactory.CANNON;
            case "7": return BuildingFactory.VILLAGE_HALL;
            default:  return null;
        }
    }

    /**
     * Maps a train-menu digit to a {@link HabitantFactory} type constant.
     *
     * @param choice single-character input
     * @return factory type constant, or {@code null} for cancel/invalid
     */
    private String mapTrainChoice(String choice) {
        switch (choice) {
            case "1": return HabitantFactory.SOLDIER;
            case "2": return HabitantFactory.ARCHER;
            case "3": return HabitantFactory.KNIGHT;
            case "4": return HabitantFactory.CATAPULT;
            case "5": return HabitantFactory.GOLD_MINER;
            case "6": return HabitantFactory.IRON_MINER;
            case "7": return HabitantFactory.LUMBERMAN;
            default:  return null;
        }
    }
}
