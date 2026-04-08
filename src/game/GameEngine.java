package game;

import gameelements.*;
import utility.*;
import exceptions.*;

import java.util.*;
import java.util.stream.*;

/**
 * Central controller that manages the game simulation.
 *
 * <p>Extended in Assignment 4 with three new methods that support the networked game
 * features: {@link #generateCompatibleVillage}, {@link #generateCompatibleArmy}, and
 * {@link #testVillageDefense}.  These are designed to run on the server's shared thread
 * pool, enabling parallel execution for multiple clients.</p>
 */
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

    /* ------------------------------------------------------------------ */
    /*  Assignment 4 – new game functionalities                            */
    /* ------------------------------------------------------------------ */

    /**
     * Generates a new NPC village whose overall strength is compatible with the given
     * reference village.  The difficulty is derived from the reference village's defence
     * score so that the generated target provides a fair challenge.
     *
     * <p>This method is submitted to the server's shared thread pool so that generating
     * villages for multiple simultaneous clients runs in parallel.</p>
     *
     * @param refVillage the player's village used as the calibration baseline
     * @return a freshly generated NPC village at a comparable strength level
     */
    public Village generateCompatibleVillage(Village refVillage) {
        // Derive a difficulty level (1-3) from the reference village's defence score
        double defScore = refVillage.getDefenceScore();
        int difficulty;
        if (defScore < 500) {
            difficulty = 1;
        } else if (defScore < 1500) {
            difficulty = 2;
        } else {
            difficulty = 3;
        }

        // Local class mirrors the NpcBuilder pattern already used in generateNpcVillages
        class CompatibleVillageBuilder {
            Village build(int diff) {
                String[] adjectives = {"Rival", "Enemy", "Hostile", "Warring", "Challenger"};
                String name = adjectives[random.nextInt(adjectives.length)]
                        + "_" + (random.nextInt(900) + 100);

                Village v = new Village(name,
                        200 + diff * 100,
                        100 + diff * 60,
                        150 + diff * 80);

                // Add defensive buildings proportional to difficulty
                for (int i = 0; i < diff; i++) {
                    v.getBuildingsMutable().add(new ArcherTower());
                }
                if (diff >= 2) {
                    v.getBuildingsMutable().add(new Cannon());
                }
                if (diff >= 3) {
                    v.getBuildingsMutable().add(new Cannon());
                    v.getBuildingsMutable().add(new GoldMine());
                }

                // Add habitants scaled to difficulty
                int fighterCount = diff * 2 + 1;
                for (int i = 0; i < Math.min(fighterCount, 5); i++) {
                    v.getHabitantsMutable().add(new Soldier());
                }
                return v;
            }
        }

        return new CompatibleVillageBuilder().build(difficulty);
    }

    /**
     * Generates a detached {@link Army} whose attack strength is compatible with the
     * defending village's current defence score.  The army is not attached to any village;
     * it is used purely for simulation/testing purposes.
     *
     * <p>This method is submitted to the server's shared thread pool so that armies for
     * multiple simultaneous clients are generated in parallel.</p>
     *
     * @param defenderVillage the village whose defence score calibrates the generated army
     * @return a newly generated army calibrated to challenge the given village
     */
    public Army generateCompatibleArmy(Village defenderVillage) {
        Army army = new Army();
        double defScore = defenderVillage.getDefenceScore();

        // Scale army composition to match the defender's strength
        int soldiers  = 1 + (int)(defScore / 300);
        int archers   = (defScore > 400)  ? 1 + (int)(defScore / 600) : 0;
        int knights   = (defScore > 800)  ? 1 + (int)(defScore / 1200) : 0;
        int catapults = (defScore > 1500) ? 1 : 0;

        // Cap total army size at Army.MAX_ARMY_SIZE
        int remaining = Army.MAX_ARMY_SIZE;

        try {
            for (int i = 0; i < Math.min(soldiers, remaining); i++) {
                army.addFighter(new Soldier());
                remaining--;
            }
            for (int i = 0; i < Math.min(archers, remaining); i++) {
                army.addFighter(new Archer());
                remaining--;
            }
            for (int i = 0; i < Math.min(knights, remaining); i++) {
                army.addFighter(new Knight());
                remaining--;
            }
            for (int i = 0; i < Math.min(catapults, remaining); i++) {
                army.addFighter(new Catapult());
                remaining--;
            }
        } catch (InvalidOperationException e) {
            // Army reached max size; this is expected for large villages
        }

        return army;
    }

    /**
     * Tests the player's village defences by generating and running three compatible
     * attack armies against it.  Each army is generated via
     * {@link #generateCompatibleArmy(Village)} and simulated using the
     * {@link utility.ChallengeDecisionAdapter} so that the same combat logic used for
     * real attacks is applied here.
     *
     * <p>Returns a formatted report showing the outcome of each simulated attack and a
     * final defence score (number of attacks repelled out of three).</p>
     *
     * <p>This method is designed to run on the server's shared thread pool.</p>
     *
     * @param village the village whose defences are being tested
     * @return a multi-line human-readable test report
     */
    public String testVillageDefense(Village village) {
        int totalRounds = 3;
        int defended = 0;

        StringBuilder report = new StringBuilder();
        report.append("=== Village Defence Test ===\n");
        report.append(String.format("Village: %s  |  Defence Score: %.1f%n%n",
                village.getName(), village.getDefenceScore()));

        ChallengeDecisionAdapter adapter = new ChallengeDecisionAdapter();

        for (int round = 1; round <= totalRounds; round++) {
            Army attacker = generateCompatibleArmy(village);
            report.append(String.format("  Round %d: Army size=%d, Attack score=%.1f%n",
                    round, attacker.size(), attacker.getAttackScore()));

            AttackOutcome outcome;
            try {
                outcome = adapter.adapt(attacker, village);
            } catch (Exception e) {
                report.append("    Error during simulation: ").append(e.getMessage()).append("\n");
                continue;
            }

            if (outcome.isSuccess()) {
                report.append("    Result: VILLAGE BREACHED – attacker succeeded\n");
            } else {
                report.append("    Result: VILLAGE HELD – defence repelled the assault\n");
                defended++;
            }
        }

        report.append(String.format("%nFinal defence score: %d / %d rounds held%n", defended, totalRounds));

        if (defended == totalRounds) {
            report.append("  EXCELLENT! Your village is well-defended.\n");
        } else if (defended >= totalRounds / 2) {
            report.append("  FAIR. Consider adding more defence buildings or upgrading them.\n");
        } else {
            report.append("  POOR. Your village needs significant defensive upgrades!\n");
        }

        return report.toString();
    }
}
