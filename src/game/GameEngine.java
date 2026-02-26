package game;

import gameelements.*;
import utility.*;

import java.util.*;
import java.util.stream.*;

/**
 * Central controller that manages the game simulation.
 * <p>
 * {@code GameEngine} is responsible for:
 * <ul>
 *   <li>Maintaining the global {@link WallClock} and game tick</li>
 *   <li>Generating random NPC villages to attack</li>
 *   <li>Managing the leaderboard using a {@link Repository}&lt;{@link Player}&gt;</li>
 *   <li>Calculating scores and determining win conditions</li>
 * </ul>
 * </p>
 * <p>
 * Demonstrates: generics ({@code Repository<Player>}), anonymous/local classes,
 * lambdas, streams, and Java Collections utilities.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class GameEngine {

    /** Maximum buildings allowed per village. */
    public static final int MAX_BUILDINGS = 20;

    /** Maximum level for any building. */
    public static final int MAX_BUILDING_LEVEL = 5;

    /** Maximum army size. */
    public static final int MAX_ARMY_SIZE = 10;

    /** Guard time in game ticks (simulates 30 seconds). */
    public static final int GUARD_TICKS = 30;

    /** The global game clock. */
    private final WallClock clock;

    /** Repository of all players keyed by player ID string. */
    private final Repository<Player> playerRepository;

    /** Repository of NPC villages available to attack. */
    private final Repository<Village> npcVillages;

    /** The game map. */
    private final GameMap gameMap;

    /** Random number generator for NPC village generation. */
    private final Random random;

    /** Running flag for the game loop. */
    private boolean running;

    /**
     * Constructs and initialises the GameEngine.
     */
    public GameEngine() {
        this.clock            = new WallClock();
        this.playerRepository = new Repository<>();
        this.npcVillages      = new Repository<>();
        this.gameMap          = new GameMap(new Region(0, 0, 1000, 1000));
        this.random           = new Random();
        this.running          = false;
    }

    /**
     * Registers a player with the game engine.
     *
     * @param player the {@link Player} to register
     */
    public void registerPlayer(Player player) {
        playerRepository.put(String.valueOf(player.getPlayerID()), player);
        gameMap.addVillage(player.getVillage().getName(),
                new Position(random.nextInt(1000), random.nextInt(1000)));
    }

    /**
     * Generates {@code count} random NPC villages and stores them in the repository.
     * <p>
     * Uses a local class {@code NpcBuilder} for construction logic and an anonymous
     * {@link Comparator} to sort building types.
     * </p>
     *
     * @param count number of NPC villages to generate
     */
    public void generateNpcVillages(int count) {

        // Local class responsible for constructing an NPC village
        class NpcBuilder {
            Village build(String villageName, int difficulty) {
                Village v = new Village(villageName,
                        200 + difficulty * 100,
                        100 + difficulty * 60,
                        150 + difficulty * 80);

                // Add random defensive buildings based on difficulty
                if (difficulty >= 1) v.getBuildingsMutable().add(new ArcherTower());
                if (difficulty >= 2) v.getBuildingsMutable().add(new Cannon());
                if (difficulty >= 2) v.getBuildingsMutable().add(new ArcherTower());
                if (difficulty >= 3) {
                    v.getBuildingsMutable().add(new Cannon());
                    v.getBuildingsMutable().add(new GoldMine());
                }

                // Add a few fighters to the NPC village for score purposes
                int fighters = difficulty * 2 + 1;
                for (int i = 0; i < Math.min(fighters, 5); i++) {
                    v.getHabitantsMutable().add(new Soldier());
                }

                return v;
            }
        }

        NpcBuilder builder = new NpcBuilder();

        String[] names = {"Oakholm", "Ironfield", "Stonekeep", "Maplewood",
                          "Ashford", "Cresthill", "Duskvale", "Emberton",
                          "Frostmere", "Goldgate"};

        for (int i = 0; i < count; i++) {
            int difficulty = random.nextInt(3) + 1;
            String key = "npc_" + i;
            String villageName = names[i % names.length] + "_" + (i + 1);
            Village v = builder.build(villageName, difficulty);
            npcVillages.put(key, v);
            gameMap.addVillage(villageName,
                    new Position(random.nextInt(1000), random.nextInt(1000)));
        }
    }

    /**
     * Returns a list of all available NPC villages sorted by defence score (ascending).
     * <p>
     * Uses an anonymous {@link Comparator} class for sorting and a {@code TreeMap}
     * internally through the {@link Repository}.
     * </p>
     *
     * @return sorted list of NPC villages
     */
    public List<Village> getAvailableTargets() {
        List<Village> targets = new ArrayList<>(npcVillages.getAll());

        // Anonymous Comparator class for sorting by defence score
        targets.sort(new Comparator<Village>() {
            @Override
            public int compare(Village a, Village b) {
                return Double.compare(a.getDefenceScore(), b.getDefenceScore());
            }
        });

        return targets;
    }

    /**
     * Advances the game clock by one tick and triggers periodic game events.
     */
    public void tick() {
        clock.tick();
    }

    /**
     * Returns the current game tick.
     *
     * @return current game time
     */
    public int getCurrentTick() {
        return clock.getTime();
    }

    /**
     * Computes the leaderboard as a sorted list of players by score.
     * <p>
     * Uses {@code Comparator.comparing()} method reference and stream operations.
     * </p>
     *
     * @return list of players sorted by score (highest first)
     */
    public List<Player> getLeaderboard() {
        return playerRepository.getAll().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    /**
     * Prints the leaderboard to standard output.
     */
    public void printLeaderboard() {
        System.out.println("===== LEADERBOARD =====");
        List<Player> board = getLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            System.out.println("  #" + (i + 1) + "  " + board.get(i).getRankingSummary());
        }
        System.out.println("=======================");
    }

    /**
     * Returns the game clock.
     *
     * @return the {@link WallClock}
     */
    public WallClock getClock() { return clock; }

    /**
     * Returns the player repository.
     *
     * @return repository of players
     */
    public Repository<Player> getPlayerRepository() { return playerRepository; }

    /**
     * Returns the NPC village repository.
     *
     * @return repository of NPC villages
     */
    public Repository<Village> getNpcVillages() { return npcVillages; }

    /**
     * Returns the game map.
     *
     * @return the {@link GameMap}
     */
    public GameMap getGameMap() { return gameMap; }

    /**
     * Sets the running flag.
     *
     * @param running {@code true} to mark the game as running
     */
    public void setRunning(boolean running) { this.running = running; }

    /**
     * Returns whether the game is currently running.
     *
     * @return {@code true} if running
     */
    public boolean isRunning() { return running; }
}
