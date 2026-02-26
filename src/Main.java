import game.*;
import gameelements.*;
import gui.*;
import exceptions.*;
import utility.*;

import java.util.*;
import java.io.*;
import java.util.stream.*;

/**
 * Entry point for the Village War Strategy Game.
 * <p>
 * This class implements a full console-based game loop that allows the player to:
 * <ol>
 *   <li>View village status</li>
 *   <li>Build new buildings</li>
 *   <li>Train military and worker units</li>
 *   <li>Upgrade buildings</li>
 *   <li>Explore NPC villages available to attack</li>
 *   <li>Attack a village</li>
 *   <li>Collect resources from production buildings and workers</li>
 *   <li>View the ranking / leaderboard</li>
 *   <li>View army composition</li>
 *   <li>Quit the game</li>
 * </ol>
 * </p>
 * <p>
 * Demonstrates: generics, anonymous classes, lambdas, method references,
 * custom exceptions, Java utility classes, and streams.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Main {

    // -------------------------------------------------------------------------
    // Game state
    // -------------------------------------------------------------------------

    /** The game engine managing global state. */
    private static GameEngine engine;

    /** The human player. */
    private static Player player;

    /** Console-based UI renderer. */
    private static GraphicalInterface ui;

    /** Resource collector. */
    private static CollectResources collector;

    /** Scanner for reading console input. */
    private static Scanner scanner;

    // -------------------------------------------------------------------------
    // Entry point
    // -------------------------------------------------------------------------

    /**
     * Application entry point. Initialises the game and starts the main loop.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        scanner   = new Scanner(new BufferedReader(new InputStreamReader(System.in)));
        ui        = new GraphicalInterface();
        engine    = new GameEngine();
        collector = new CollectResources();

        printWelcomeBanner();

        // Create the player's village
        System.out.print("  Enter your village name: ");
        String villageName = scanner.nextLine().trim();
        if (villageName.isEmpty()) villageName = "MyVillage";

        player = new Player(1, villageName);
        engine.registerPlayer(player);
        engine.generateNpcVillages(5);
        engine.setRunning(true);

        System.out.println("\n  Village '" + villageName + "' created!");
        System.out.println("  Starting resources: 500 Gold, 300 Iron, 400 Lumber");
        System.out.println("  Buildings: 1 Village Hall + 1 Farm");

        // Main game loop
        while (engine.isRunning()) {
            engine.tick();
            ui.renderMenu();

            String input = scanner.nextLine().trim();
            System.out.println();

            try {
                handleMenuChoice(input);
            } catch (InsufficientResourcesException e) {
                System.out.println("  [!] " + e.getMessage());
            } catch (MaxLevelReachedException e) {
                System.out.println("  [!] " + e.getMessage());
            } catch (BuildingLimitExceededException e) {
                System.out.println("  [!] " + e.getMessage());
            } catch (InvalidOperationException e) {
                System.out.println("  [!] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("  [!] Unexpected error: " + e.getMessage());
            }
        }

        System.out.println("\n  Thanks for playing! Final score: " + player.getScore());
        engine.printLeaderboard();
    }

    // -------------------------------------------------------------------------
    // Menu dispatcher
    // -------------------------------------------------------------------------

    /**
     * Dispatches the player's menu choice to the appropriate handler.
     *
     * @param choice the raw input string from the player
     * @throws InsufficientResourcesException  if a resource operation fails
     * @throws MaxLevelReachedException        if a max-level upgrade is attempted
     * @throws BuildingLimitExceededException  if the building cap is exceeded
     * @throws InvalidOperationException       if an illegal action is attempted
     */
    private static void handleMenuChoice(String choice)
            throws InsufficientResourcesException, MaxLevelReachedException,
                   BuildingLimitExceededException, InvalidOperationException {

        switch (choice) {
            case "1": viewVillage();     break;
            case "2": buildBuilding();   break;
            case "3": trainUnit();       break;
            case "4": upgradeBuilding(); break;
            case "5": exploreTargets();  break;
            case "6": attackVillage();   break;
            case "7": collectResources();break;
            case "8": viewRanking();     break;
            case "9": viewArmy();        break;
            case "0": engine.setRunning(false); break;
            default:
                System.out.println("  Invalid choice. Please enter 0-9.");
        }
    }

    // -------------------------------------------------------------------------
    // Option 1: View village
    // -------------------------------------------------------------------------

    /**
     * Displays the player's village status.
     */
    private static void viewVillage() {
        ui.renderVillage(player.getVillage());
    }

    // -------------------------------------------------------------------------
    // Option 2: Build a building
    // -------------------------------------------------------------------------

    /**
     * Presents the build menu and builds the selected building.
     *
     * @throws BuildingLimitExceededException  if building limit is reached
     * @throws InsufficientResourcesException  if resources are insufficient
     */
    private static void buildBuilding()
            throws BuildingLimitExceededException, InsufficientResourcesException {

        System.out.println("  BUILD MENU");
        System.out.println("  ----------");
        System.out.println("  1. Farm             (50G / 30I / 20L)");
        System.out.println("  2. Gold Mine        (100G / 50I / 50L)");
        System.out.println("  3. Iron Mine        (80G / 60I / 40L)");
        System.out.println("  4. Lumber Mill      (30G / 10I / 60L)");
        System.out.println("  5. Archer Tower     (60G / 40I / 30L)");
        System.out.println("  6. Cannon           (100G / 80I / 40L)");
        System.out.println("  7. Village Hall     (200G / 100I / 100L)");
        System.out.println("  0. Cancel");
        System.out.print("  Choice: ");

        String choice = scanner.nextLine().trim();
        System.out.println();

        Building building = null;
        double costGold = 0, costIron = 0, costLumber = 0;

        switch (choice) {
            case "1":
                building = new Farm();
                costGold = Farm.COST_GOLD; costIron = Farm.COST_IRON; costLumber = Farm.COST_LUMBER;
                break;
            case "2":
                building = new GoldMine();
                costGold = GoldMine.COST_GOLD; costIron = GoldMine.COST_IRON; costLumber = GoldMine.COST_LUMBER;
                break;
            case "3":
                building = new IronMine();
                costGold = IronMine.COST_GOLD; costIron = IronMine.COST_IRON; costLumber = IronMine.COST_LUMBER;
                break;
            case "4":
                building = new LumberMill();
                costGold = LumberMill.COST_GOLD; costIron = LumberMill.COST_IRON; costLumber = LumberMill.COST_LUMBER;
                break;
            case "5":
                building = new ArcherTower();
                costGold = ArcherTower.COST_GOLD; costIron = ArcherTower.COST_IRON; costLumber = ArcherTower.COST_LUMBER;
                break;
            case "6":
                building = new Cannon();
                costGold = Cannon.COST_GOLD; costIron = Cannon.COST_IRON; costLumber = Cannon.COST_LUMBER;
                break;
            case "7":
                building = new VillageHall();
                costGold = VillageHall.COST_GOLD; costIron = VillageHall.COST_IRON; costLumber = VillageHall.COST_LUMBER;
                break;
            case "0":
                System.out.println("  Cancelled.");
                return;
            default:
                System.out.println("  Invalid choice.");
                return;
        }

        Village village = player.getVillage();

        // Check resources manually before calling village.build()
        if (!village.getGold().hasEnough(costGold)) {
            throw new InsufficientResourcesException("Gold", costGold, village.getGold().getQuantity());
        }
        if (!village.getIron().hasEnough(costIron)) {
            throw new InsufficientResourcesException("Iron", costIron, village.getIron().getQuantity());
        }
        if (!village.getLumber().hasEnough(costLumber)) {
            throw new InsufficientResourcesException("Lumber", costLumber, village.getLumber().getQuantity());
        }

        if (village.getBuildings().size() >= Village.MAX_BUILDINGS) {
            throw new BuildingLimitExceededException(village.getBuildings().size(), Village.MAX_BUILDINGS);
        }

        // Deduct resources and add building directly
        village.getGold().subtract(costGold);
        village.getIron().subtract(costIron);
        village.getLumber().subtract(costLumber);
        village.getBuildingsMutable().add(building);

        System.out.println("  Built: " + building.getName());
        player.recordAttack(10, true); // award construction points
    }

    // -------------------------------------------------------------------------
    // Option 3: Train a unit
    // -------------------------------------------------------------------------

    /**
     * Presents the train menu and trains the selected unit type.
     *
     * @throws InsufficientResourcesException if resources are insufficient
     */
    private static void trainUnit() throws InsufficientResourcesException {
        System.out.println("  TRAIN MENU");
        System.out.println("  ----------");
        System.out.println("  FIGHTERS:");
        System.out.println("  1. Soldier    (20G / 10I / 5L)");
        System.out.println("  2. Archer     (25G / 5I / 15L)");
        System.out.println("  3. Knight     (50G / 30I / 10L)");
        System.out.println("  4. Catapult   (80G / 60I / 50L)");
        System.out.println("  WORKERS:");
        System.out.println("  5. Gold Miner (free)");
        System.out.println("  6. Iron Miner (free)");
        System.out.println("  7. Lumberman  (free)");
        System.out.println("  0. Cancel");
        System.out.print("  Choice: ");

        String choice = scanner.nextLine().trim();
        System.out.println();

        Habitant habitant = null;
        double cg = 0, ci = 0, cl = 0;

        switch (choice) {
            case "1": habitant = new Soldier();   cg = Soldier.COST_GOLD;   ci = Soldier.COST_IRON;   cl = Soldier.COST_LUMBER;   break;
            case "2": habitant = new Archer();    cg = Archer.COST_GOLD;    ci = Archer.COST_IRON;    cl = Archer.COST_LUMBER;    break;
            case "3": habitant = new Knight();    cg = Knight.COST_GOLD;    ci = Knight.COST_IRON;    cl = Knight.COST_LUMBER;    break;
            case "4": habitant = new Catapult();  cg = Catapult.COST_GOLD;  ci = Catapult.COST_IRON;  cl = Catapult.COST_LUMBER;  break;
            case "5": habitant = new GoldMiner(); break;
            case "6": habitant = new IronMiner(); break;
            case "7": habitant = new Lumberman(); break;
            case "0": System.out.println("  Cancelled."); return;
            default:  System.out.println("  Invalid choice."); return;
        }

        player.getVillage().train(habitant, cg, ci, cl);
        System.out.println("  Trained: " + habitant.getClass().getSimpleName());

        // If it's a fighter, offer to add it to the army
        if (habitant instanceof Fighter) {
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

    // -------------------------------------------------------------------------
    // Option 4: Upgrade a building
    // -------------------------------------------------------------------------

    /**
     * Presents the upgrade menu and upgrades the selected building.
     *
     * @throws MaxLevelReachedException       if the building is already at max level
     * @throws InsufficientResourcesException if resources are insufficient
     * @throws InvalidOperationException      if the building is not valid
     */
    private static void upgradeBuilding()
            throws MaxLevelReachedException, InsufficientResourcesException,
                   InvalidOperationException {

        List<Building> buildings = new ArrayList<>(player.getVillage().getBuildings());
        if (buildings.isEmpty()) {
            System.out.println("  No buildings to upgrade.");
            return;
        }

        System.out.println("  UPGRADE MENU  (VillageHall Lv = "
                + player.getVillage().getVillageHall().getLevel() + ")");
        System.out.println("  --------------------------------------------------------");

        for (int i = 0; i < buildings.size(); i++) {
            Building b = buildings.get(i);
            System.out.printf("  [%2d] %-20s Lv%d  UpgCost: %.0fG/%.0fI/%.0fL%n",
                    i + 1, b.getName(), b.getLevel(),
                    b.getUpgradeCostGold(), b.getUpgradeCostIron(), b.getUpgradeCostLumber());
        }
        System.out.println("  [0]  Cancel");
        System.out.print("  Choice: ");

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

    // -------------------------------------------------------------------------
    // Option 5: Explore targets
    // -------------------------------------------------------------------------

    /**
     * Displays a list of NPC villages available to attack.
     */
    private static void exploreTargets() {
        List<Village> targets = engine.getAvailableTargets();
        ui.renderTargets(targets);
    }

    // -------------------------------------------------------------------------
    // Option 6: Attack
    // -------------------------------------------------------------------------

    /**
     * Prompts the player to select a target and performs the attack.
     *
     * @throws InvalidOperationException if the army is empty or selection is invalid
     */
    private static void attackVillage() throws InvalidOperationException {
        if (player.getArmy().isEmpty()) {
            throw new InvalidOperationException("attack", "Your army is empty. Train fighters first.");
        }

        List<Village> targets = engine.getAvailableTargets();
        if (targets.isEmpty()) {
            System.out.println("  No targets available.");
            return;
        }

        ui.renderTargets(targets);
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
            int points = (int)(outcome.getNewGold() + outcome.getNewIron() + outcome.getNewLumber());
            player.recordAttack(points + 50, true);
            System.out.printf("  Resources gained: +%.1f Gold, +%.1f Iron, +%.1f Lumber%n",
                    outcome.getNewGold(), outcome.getNewIron(), outcome.getNewLumber());
            player.notify(target);
        } else {
            player.recordAttack(0, false);
        }
    }

    // -------------------------------------------------------------------------
    // Option 7: Collect resources
    // -------------------------------------------------------------------------

    /**
     * Runs the resource collection cycle for the player's village.
     */
    private static void collectResources() {
        String result = collector.collect(player.getVillage());
        System.out.println("  " + result);
    }

    // -------------------------------------------------------------------------
    // Option 8: View ranking
    // -------------------------------------------------------------------------

    /**
     * Prints the leaderboard and the player's current statistics.
     */
    private static void viewRanking() {
        engine.printLeaderboard();
        System.out.println("  " + player.getRankingSummary());
        System.out.printf("  Village defence score : %.1f%n",
                player.getVillage().getDefenceScore());
        System.out.printf("  Army attack score     : %.1f%n",
                player.getArmy().getAttackScore());
    }

    // -------------------------------------------------------------------------
    // Option 9: View army
    // -------------------------------------------------------------------------

    /**
     * Renders the current army composition.
     */
    private static void viewArmy() {
        ui.renderArmy(player.getArmy());
    }

    // -------------------------------------------------------------------------
    // Utilities
    // -------------------------------------------------------------------------

    /**
     * Prints the game's welcome banner using a lambda action.
     */
    private static void printWelcomeBanner() {
        // Use an anonymous Runnable (demonstrates anonymous class usage)
        Runnable banner = new Runnable() {
            @Override
            public void run() {
                System.out.println("============================================================");
                System.out.println("       VILLAGE WAR STRATEGY GAME  (COSC 3P91 A2)           ");
                System.out.println("============================================================");
                System.out.println("  Build your village, train your army, and conquer enemies! ");
                System.out.println("============================================================");
                System.out.println();
            }
        };
        banner.run();
    }
}
