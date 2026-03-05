package game;

import gameelements.*;
import utility.*;

import java.util.*;
import java.util.stream.*;

// Central controller that manages the game simulation.
public class GameEngine {

    public static final int MAX_BUILDINGS = 20;
    public static final int MAX_BUILDING_LEVEL = 5;
    public static final int MAX_ARMY_SIZE = 10;
    public static final int GUARD_TICKS = 30;

    private final WallClock clock;
    private final Repository<Player> playerRepository;
    private final Repository<Village> npcVillages;
    private final GameMap gameMap;
    private final Random random;
    private boolean running;

    public GameEngine() {
        this.clock            = new WallClock();
        this.playerRepository = new Repository<>();
        this.npcVillages      = new Repository<>();
        this.gameMap          = new GameMap(new Region(0, 0, 1000, 1000));
        this.random           = new Random();
        this.running          = false;
    }

    public void registerPlayer(Player player) {
        playerRepository.put(String.valueOf(player.getPlayerID()), player);
        gameMap.addVillage(player.getVillage().getName(),
                new Position(random.nextInt(1000), random.nextInt(1000)));
    }

    // Generates NPC villages using a local class for construction and an anonymous Comparator for sorting.
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

    // Returns NPC villages sorted by defence score ascending using an anonymous Comparator.
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

    public void tick() {
        clock.tick();
    }

    public int getCurrentTick() {
        return clock.getTime();
    }

    // Returns players sorted by score descending using Comparator.comparing + stream.
    public List<Player> getLeaderboard() {
        return playerRepository.getAll().stream()
                .sorted(Comparator.comparingInt(Player::getScore).reversed())
                .collect(Collectors.toList());
    }

    public void printLeaderboard() {
        System.out.println("===== LEADERBOARD =====");
        List<Player> board = getLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            System.out.println("  #" + (i + 1) + "  " + board.get(i).getRankingSummary());
        }
        System.out.println("=======================");
    }

    public WallClock getClock() { return clock; }
    public Repository<Player> getPlayerRepository() { return playerRepository; }
    public Repository<Village> getNpcVillages() { return npcVillages; }
    public GameMap getGameMap() { return gameMap; }
    public void setRunning(boolean running) { this.running = running; }
    public boolean isRunning() { return running; }
}
