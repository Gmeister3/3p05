package server;

import controller.GameController;
import exceptions.*;
import factory.BuildingFactory;
import factory.HabitantFactory;
import game.*;
import gameelements.*;
import network.JsonHelper;
import network.Message;
import network.MessageType;
import persistence.VillageSerializer;
import utility.AttackOutcome;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

/**
 * Handles the complete lifecycle of a single client connection in its own thread.
 *
 * <p><b>Threading model</b><br>
 * Each accepted TCP connection spawns a new {@code ClientHandler} thread.  Heavy game
 * operations (generating compatible villages/armies, running attacks, testing village
 * defences) are submitted to a shared {@link ExecutorService} thread pool so that the
 * per-client thread stays responsive and the server can parallelise those CPU-intensive
 * tasks across all connected clients.</p>
 *
 * <p><b>Protocol</b><br>
 * Messages are exchanged as single-line JSON strings terminated by {@code '\n'}.
 * The handler reads one line per iteration, parses it with {@link JsonHelper#fromJson},
 * dispatches to the appropriate handler method, and writes a JSON reply with
 * {@link JsonHelper#toJson}.  The session is terminated when the client sends
 * {@link MessageType#QUIT} or when the socket closes.</p>
 *
 * <p><b>Authentication</b><br>
 * The very first message <em>must</em> be either {@link MessageType#LOGIN} or
 * {@link MessageType#REGISTER}.  All other message types are rejected until the
 * handshake succeeds.</p>
 */
public class ClientHandler implements Runnable {

    /** The accepted client socket. */
    private final Socket socket;

    /** Shared user database (thread-safe). */
    private final PlayerDatabase database;

    /** Shared thread pool for heavy game operations. */
    private final ExecutorService pool;

    /** Unique connection counter used to assign playerIDs. */
    private final int connectionId;

    /** Per-session game state; {@code null} until the client authenticates. */
    private GameSession session;

    /** Factories reused across requests. */
    private final BuildingFactory buildingFactory = new BuildingFactory();
    private final HabitantFactory habitantFactory = new HabitantFactory();

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Creates a handler for the given client socket.
     *
     * @param socket       the accepted client connection
     * @param database     shared player authentication database
     * @param pool         shared executor service for heavy tasks
     * @param connectionId numeric identifier for this connection (used as playerID)
     */
    public ClientHandler(Socket socket, PlayerDatabase database,
                         ExecutorService pool, int connectionId) {
        this.socket       = socket;
        this.database     = database;
        this.pool         = pool;
        this.connectionId = connectionId;
    }

    /* ------------------------------------------------------------------ */
    /*  Runnable – main loop                                               */
    /* ------------------------------------------------------------------ */

    /**
     * Reads incoming JSON messages from the client, processes them, and writes JSON
     * responses until the connection is closed or a QUIT message is received.
     */
    @Override
    public void run() {
        System.out.println("[Server] Client #" + connectionId + " connected from "
                + socket.getRemoteSocketAddress());

        try (
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                Message request;
                try {
                    request = JsonHelper.fromJson(line);
                } catch (IllegalArgumentException e) {
                    send(out, Message.error("Malformed JSON: " + e.getMessage()));
                    continue;
                }

                // Always allow LOGIN and REGISTER even when not authenticated
                if (request.getType() == MessageType.LOGIN) {
                    handleLogin(out, request);
                    continue;
                }
                if (request.getType() == MessageType.REGISTER) {
                    handleRegister(out, request);
                    continue;
                }

                // All other requests require an active session
                if (session == null) {
                    send(out, Message.error("Not authenticated. Please LOGIN first."));
                    continue;
                }

                // Advance the game clock on every request
                session.tick();

                boolean shouldQuit = dispatch(out, request);
                if (shouldQuit) break;
            }
        } catch (IOException e) {
            System.out.println("[Server] Client #" + connectionId
                    + " disconnected: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) { }
            System.out.println("[Server] Client #" + connectionId + " session ended.");
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Message dispatcher                                                 */
    /* ------------------------------------------------------------------ */

    /**
     * Routes an authenticated request to the correct handler method.
     *
     * @param out     the client's output writer
     * @param request the parsed request message
     * @return {@code true} if the server should close this connection (QUIT)
     */
    private boolean dispatch(PrintWriter out, Message request) {
        switch (request.getType()) {
            case VIEW_VILLAGE:  handleViewVillage(out);                  break;
            case BUILD:         handleBuild(out, request);               break;
            case TRAIN:         handleTrain(out, request);               break;
            case UPGRADE:       handleUpgrade(out, request);             break;
            case EXPLORE:       handleExplore(out);                      break;
            case ATTACK:        handleAttack(out, request);              break;
            case COLLECT:       handleCollect(out);                      break;
            case VIEW_RANKING:  handleViewRanking(out);                  break;
            case VIEW_ARMY:     handleViewArmy(out);                     break;
            case GENERATE_ARMY: handleGenerateArmy(out);                 break;
            case TEST_VILLAGE:  handleTestVillage(out);                  break;
            case SAVE:          handleSave(out);                         break;
            case LOAD:          handleLoad(out);                         break;
            case QUIT:
                send(out, Message.ok("Goodbye, " + session.getUsername() + "!"));
                return true;
            default:
                send(out, Message.error("Unknown request type: " + request.getType()));
        }
        return false;
    }

    /* ------------------------------------------------------------------ */
    /*  Authentication handlers                                            */
    /* ------------------------------------------------------------------ */

    /**
     * Handles a LOGIN request.  Creates a new {@link GameSession} on success.
     *
     * @param out     client output writer
     * @param request the LOGIN message
     */
    private void handleLogin(PrintWriter out, Message request) {
        String username = request.getOrDefault("username", "");
        String password = request.getOrDefault("password", "");

        if (database.authenticate(username, password)) {
            session = new GameSession(username, connectionId);
            Message reply = new Message.Builder(MessageType.LOGIN_OK)
                    .put("message", "Welcome, " + username + "! Village ready.")
                    .build();
            send(out, reply);
            System.out.println("[Server] Client #" + connectionId
                    + " authenticated as '" + username + "'");
        } else {
            Message reply = new Message.Builder(MessageType.LOGIN_FAIL)
                    .put("reason", "Invalid username or password.")
                    .build();
            send(out, reply);
        }
    }

    /**
     * Handles a REGISTER request.  Adds the account to the database and creates a session.
     *
     * @param out     client output writer
     * @param request the REGISTER message
     */
    private void handleRegister(PrintWriter out, Message request) {
        String username = request.getOrDefault("username", "");
        String password = request.getOrDefault("password", "");

        if (database.register(username, password)) {
            session = new GameSession(username, connectionId);
            Message reply = new Message.Builder(MessageType.REGISTER_OK)
                    .put("message", "Account created. Welcome, " + username + "!")
                    .build();
            send(out, reply);
            System.out.println("[Server] Registered new player '" + username + "'");
        } else {
            Message reply = new Message.Builder(MessageType.REGISTER_FAIL)
                    .put("reason", "Username already taken or invalid.")
                    .build();
            send(out, reply);
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Game handlers                                                      */
    /* ------------------------------------------------------------------ */

    /** Sends the current village status to the client. */
    private void handleViewVillage(PrintWriter out) {
        send(out, Message.ok(session.getVillage().getStatusSummary()));
    }

    /**
     * Builds a new building for the player.
     *
     * @param out     client output writer
     * @param request request with {@code "buildingType"} field
     */
    private void handleBuild(PrintWriter out, Message request) {
        String type = request.getOrDefault("buildingType", "");
        Village village = session.getVillage();
        try {
            double goldCost   = buildingFactory.getGoldCost(type);
            double ironCost   = buildingFactory.getIronCost(type);
            double lumberCost = buildingFactory.getLumberCost(type);

            if (!village.getGold().hasEnough(goldCost))
                throw new InsufficientResourcesException("Gold", goldCost, village.getGold().getQuantity());
            if (!village.getIron().hasEnough(ironCost))
                throw new InsufficientResourcesException("Iron", ironCost, village.getIron().getQuantity());
            if (!village.getLumber().hasEnough(lumberCost))
                throw new InsufficientResourcesException("Lumber", lumberCost, village.getLumber().getQuantity());
            if (village.getBuildings().size() >= Village.MAX_BUILDINGS)
                throw new BuildingLimitExceededException(village.getBuildings().size(), Village.MAX_BUILDINGS);

            Building building = buildingFactory.create(type);
            village.getGold().subtract(goldCost);
            village.getIron().subtract(ironCost);
            village.getLumber().subtract(lumberCost);
            village.getBuildingsMutable().add(building);
            session.getPlayer().recordAttack(10, true);
            send(out, Message.ok("Built: " + building.getName()));

        } catch (IllegalArgumentException e) {
            send(out, Message.error("Unknown building type: " + type));
        } catch (InsufficientResourcesException | BuildingLimitExceededException e) {
            send(out, Message.error(e.getMessage()));
        }
    }

    /**
     * Trains a new unit for the player.
     *
     * @param out     client output writer
     * @param request request with {@code "unitType"} and optional {@code "addToArmy"} fields
     */
    private void handleTrain(PrintWriter out, Message request) {
        String type = request.getOrDefault("unitType", "");
        boolean addToArmy = "true".equalsIgnoreCase(request.getOrDefault("addToArmy", "false"));

        try {
            double goldCost   = habitantFactory.getGoldCost(type);
            double ironCost   = habitantFactory.getIronCost(type);
            double lumberCost = habitantFactory.getLumberCost(type);
            Habitant habitant = habitantFactory.create(type);
            session.getVillage().train(habitant, goldCost, ironCost, lumberCost);

            StringBuilder sb = new StringBuilder("Trained: " + type);
            if (habitantFactory.isFighter(type) && addToArmy) {
                try {
                    session.getArmy().addFighter((Fighter) habitant);
                    sb.append(" (added to army)");
                } catch (InvalidOperationException e) {
                    sb.append(" [army full – not added]");
                }
            }
            send(out, Message.ok(sb.toString()));

        } catch (IllegalArgumentException e) {
            send(out, Message.error("Unknown unit type: " + type));
        } catch (InsufficientResourcesException e) {
            send(out, Message.error(e.getMessage()));
        }
    }

    /**
     * Upgrades a building at the given index.
     *
     * @param out     client output writer
     * @param request request with {@code "buildingIndex"} (1-based) field
     */
    private void handleUpgrade(PrintWriter out, Message request) {
        String idxStr = request.getOrDefault("buildingIndex", "0");
        int idx;
        try {
            idx = Integer.parseInt(idxStr) - 1;
        } catch (NumberFormatException e) {
            send(out, Message.error("Invalid building index: " + idxStr));
            return;
        }

        List<Building> buildings = session.getVillage().getBuildings();
        if (idx < 0 || idx >= buildings.size()) {
            send(out, Message.error("Building index out of range."));
            return;
        }

        try {
            Building b = buildings.get(idx);
            session.getVillage().upgrade(b);
            session.getPlayer().recordAttack(20, true);
            send(out, Message.ok("Upgraded " + b.getName() + " to level " + b.getLevel()));
        } catch (MaxLevelReachedException | InvalidOperationException
                | InsufficientResourcesException e) {
            send(out, Message.error(e.getMessage()));
        }
    }

    /** Sends a list of available NPC target villages to the client. */
    private void handleExplore(PrintWriter out) {
        List<Village> targets = session.getEngine().getAvailableTargets();
        if (targets.isEmpty()) {
            send(out, Message.ok("No targets available."));
            return;
        }
        StringBuilder sb = new StringBuilder("Available targets:\n");
        for (int i = 0; i < targets.size(); i++) {
            Village v = targets.get(i);
            sb.append(String.format("  [%d] %-20s  DefScore: %6.1f  Gold: %.0f%n",
                    i + 1, v.getName(), v.getDefenceScore(), v.getGold().getQuantity()));
        }
        send(out, Message.ok(sb.toString()));
    }

    /**
     * Executes an attack on the chosen target village.
     *
     * <p>The attack is submitted to the shared thread pool so that multiple concurrent
     * client attacks are executed in parallel on separate pool threads.</p>
     *
     * @param out     client output writer
     * @param request request with {@code "targetIndex"} (1-based) field
     */
    private void handleAttack(PrintWriter out, Message request) {
        if (session.getArmy().isEmpty()) {
            send(out, Message.error("Your army is empty. Train fighters first."));
            return;
        }

        List<Village> targets = session.getEngine().getAvailableTargets();
        String idxStr = request.getOrDefault("targetIndex", "0");
        int idx;
        try {
            idx = Integer.parseInt(idxStr) - 1;
        } catch (NumberFormatException e) {
            send(out, Message.error("Invalid target index: " + idxStr));
            return;
        }

        if (idx < 0 || idx >= targets.size()) {
            send(out, Message.error("Target index out of range."));
            return;
        }

        Village target = targets.get(idx);

        // Submit attack to thread pool for parallel execution
        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() {
                try {
                    AttackOutcome outcome = session.getArmy().attack(target);
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("Attacking '%s'...%n", target.getName()));
                    sb.append(String.format("  Your attack score: %.1f | Their defence: %.1f%n",
                            session.getArmy().getAttackScore(), target.getDefenceScore()));
                    sb.append("  ").append(outcome.getMessage()).append("\n");
                    if (outcome.isSuccess()) {
                        session.getVillage().addResources(
                                outcome.getNewGold(), outcome.getNewIron(), outcome.getNewLumber());
                        int pts = (int)(outcome.getNewGold() + outcome.getNewIron() + outcome.getNewLumber());
                        session.getPlayer().recordAttack(pts + 50, true);
                        sb.append(String.format("  Loot: +%.1fG +%.1fI +%.1fL%n",
                                outcome.getNewGold(), outcome.getNewIron(), outcome.getNewLumber()));
                    } else {
                        session.getPlayer().recordAttack(0, false);
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "Attack error: " + e.getMessage();
                }
            }
        });

        try {
            String result = future.get();
            send(out, Message.ok(result));
        } catch (Exception e) {
            send(out, Message.error("Attack failed: " + e.getMessage()));
        }
    }

    /** Collects resources from the player's workers and production buildings. */
    private void handleCollect(PrintWriter out) {
        CollectResources collector = new CollectResources();
        String result = collector.collect(session.getVillage());
        send(out, Message.ok(result));
    }

    /** Sends the leaderboard and player ranking to the client. */
    private void handleViewRanking(PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        sb.append("===== LEADERBOARD =====\n");
        List<game.Player> board = session.getEngine().getLeaderboard();
        for (int i = 0; i < board.size(); i++) {
            sb.append("  #").append(i + 1).append("  ")
              .append(board.get(i).getRankingSummary()).append("\n");
        }
        sb.append("=======================\n");
        sb.append(session.getPlayer().getRankingSummary()).append("\n");
        sb.append(String.format("  Village defence score : %.1f%n",
                session.getVillage().getDefenceScore()));
        sb.append(String.format("  Army attack score     : %.1f%n",
                session.getArmy().getAttackScore()));
        send(out, Message.ok(sb.toString()));
    }

    /** Sends the army composition summary to the client. */
    private void handleViewArmy(PrintWriter out) {
        send(out, Message.ok(session.getArmy().getSummary()));
    }

    /**
     * Generates a detached army compatible with the player's current village level
     * and returns its summary.  The generation is submitted to the thread pool.
     * (NEW – Assignment 4)
     *
     * @param out client output writer
     */
    private void handleGenerateArmy(PrintWriter out) {
        Village village = session.getVillage();

        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() {
                Army generatedArmy = session.getEngine().generateCompatibleArmy(village);
                return "=== Generated Test Army ===\n"
                        + generatedArmy.getSummary()
                        + String.format("  This army is calibrated to challenge your"
                        + " village (def score: %.1f)%n", village.getDefenceScore());
            }
        });

        try {
            send(out, Message.ok(future.get()));
        } catch (Exception e) {
            send(out, Message.error("Army generation failed: " + e.getMessage()));
        }
    }

    /**
     * Tests the player's village defences by generating and running multiple compatible
     * attack armies against it.  The test runs on the shared thread pool.
     * (NEW – Assignment 4)
     *
     * @param out client output writer
     */
    private void handleTestVillage(PrintWriter out) {
        Village village = session.getVillage();

        Future<String> future = pool.submit(new Callable<String>() {
            @Override
            public String call() {
                return session.getEngine().testVillageDefense(village);
            }
        });

        try {
            send(out, Message.ok(future.get()));
        } catch (Exception e) {
            send(out, Message.error("Village test failed: " + e.getMessage()));
        }
    }

    /** Saves the player's village to an XML file. */
    private void handleSave(PrintWriter out) {
        try {
            String filename = "village_" + session.getUsername() + "_save.xml";
            VillageSerializer.save(session.getVillage(), filename);
            send(out, Message.ok("Village saved to " + filename));
        } catch (IOException e) {
            send(out, Message.error("Save failed: " + e.getMessage()));
        }
    }

    /** Loads the player's village from an XML file. */
    private void handleLoad(PrintWriter out) {
        try {
            String filename = "village_" + session.getUsername() + "_save.xml";
            Village loaded = VillageSerializer.load(filename);
            session.getPlayer().setVillage(loaded);
            send(out, Message.ok("Village loaded: " + loaded.getName()
                    + "\n" + loaded.getStatusSummary()));
        } catch (IOException | RuntimeException e) {
            send(out, Message.error("Load failed: " + e.getMessage()));
        }
    }

    /* ------------------------------------------------------------------ */
    /*  I/O helper                                                         */
    /* ------------------------------------------------------------------ */

    /**
     * Serializes {@code message} to JSON and writes it as a single line to the client.
     *
     * @param out     the client's output writer
     * @param message the message to send
     */
    private void send(PrintWriter out, Message message) {
        out.println(JsonHelper.toJson(message));
    }
}
