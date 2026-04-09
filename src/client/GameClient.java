package client;

import network.JsonHelper;
import network.Message;
import network.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * TCP client for the Village War Strategy Game (COSC 3P91 Assignment 4).
 *
 * <p>Connects to the {@link server.GameServer} over a TCP socket and implements the
 * complete client-side game loop: authentication handshake → main menu → game commands.</p>
 *
 * <h2>Protocol</h2>
 * <p>Messages are exchanged as single-line JSON strings (one message per line).
 * The client serialises a {@link Message} via {@link JsonHelper#toJson}, sends it,
 * then blocks on {@link BufferedReader#readLine()} until the server replies.
 * The reply is parsed with {@link JsonHelper#fromJson} and its {@code "message"} field
 * is displayed to the user.</p>
 *
 * <h2>Authentication handshake</h2>
 * <p>Before any game command is sent, the client prompts for a username and password
 * and sends a {@link MessageType#LOGIN} (or {@link MessageType#REGISTER}) message.
 * The server checks the credentials against the {@link server.PlayerDatabase} and replies
 * with {@link MessageType#LOGIN_OK} or {@link MessageType#LOGIN_FAIL}.  Only on
 * {@code LOGIN_OK} does the client proceed to the main game loop.</p>
 *
 * <h2>Main menu</h2>
 * <p>After authentication, the client presents the same console menu as Assignment 3,
 * extended with two new options:</p>
 * <ul>
 *   <li><b>a</b> – Generate a compatible army (tests player's defensive capability)</li>
 *   <li><b>b</b> – Test village defences against multiple generated armies</li>
 * </ul>
 */
public class GameClient {

    /** Default server host. */
    public static final String DEFAULT_HOST = "localhost";

    /** Default server port (must match {@code GameServer.DEFAULT_PORT}). */
    public static final int DEFAULT_PORT = 9090;

    /* ------------------------------------------------------------------ */
    /*  Fields                                                             */
    /* ------------------------------------------------------------------ */

    private final String host;
    private final int    port;

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Creates a client configured to connect to the given host and port.
     *
     * @param host server hostname or IP address
     * @param port TCP port the server is listening on
     */
    public GameClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /* ------------------------------------------------------------------ */
    /*  Public entry point                                                 */
    /* ------------------------------------------------------------------ */

    /**
     * Connects to the server, authenticates, and runs the interactive game loop.
     *
     * <p>This method blocks until the player quits or the connection is lost.</p>
     */
    public void run() {
        System.out.println("============================================================");
        System.out.println("       VILLAGE WAR STRATEGY GAME  (COSC 3P91 A4)           ");
        System.out.println("============================================================");
        System.out.println("  Connecting to " + host + ":" + port + " ...");

        try (
            Socket socket = new Socket(host, port);
            BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter    out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            Scanner        scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)))
        ) {
            System.out.println("  Connected!\n");

            // ── Authentication phase ──
            if (!authenticate(in, out, scanner)) {
                System.out.println("  Authentication failed. Exiting.");
                return;
            }

            // ── Game loop ──
            gameLoop(in, out, scanner);

        } catch (IOException e) {
            System.err.println("  [!] Connection error: " + e.getMessage());
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Authentication                                                     */
    /* ------------------------------------------------------------------ */

    /**
     * Handles the login/register handshake with the server.
     *
     * @param in      server input stream
     * @param out     server output stream
     * @param scanner console input
     * @return {@code true} if authentication succeeded
     * @throws IOException on I/O error
     */
    private boolean authenticate(BufferedReader in, PrintWriter out, Scanner scanner)
            throws IOException {

        while (true) {
            System.out.println("  1. Login");
            System.out.println("  2. Register");
            System.out.println("  0. Exit");
            System.out.print("  Choice: ");
            String choice = scanner.nextLine().trim();

            if ("0".equals(choice)) return false;

            MessageType requestType;
            if ("2".equals(choice)) {
                requestType = MessageType.REGISTER;
            } else {
                requestType = MessageType.LOGIN;
            }

            System.out.print("  Username: ");
            String username = scanner.nextLine().trim();
            System.out.print("  Password: ");
            String password = scanner.nextLine().trim();

            Message request = new Message.Builder(requestType)
                    .put("username", username)
                    .put("password", password)
                    .build();

            sendMessage(out, request);

            Message response = receiveMessage(in);
            if (response == null) {
                System.out.println("  [!] Connection closed by server.");
                return false;
            }

            System.out.println("\n  " + response.getOrDefault("message",
                    response.getOrDefault("reason", "")) + "\n");

            if (response.getType() == MessageType.LOGIN_OK
                    || response.getType() == MessageType.REGISTER_OK) {
                return true;
            }
            // On LOGIN_FAIL / REGISTER_FAIL, loop back and ask again
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Game loop                                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Runs the main game loop.  Presents the menu, reads the player's choice,
     * sends the corresponding request to the server, and displays the reply.
     *
     * @param in      server input stream
     * @param out     server output stream
     * @param scanner console input
     * @throws IOException on I/O error
     */
    private void gameLoop(BufferedReader in, PrintWriter out, Scanner scanner)
            throws IOException {

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();

            Message request = buildRequest(choice, scanner);
            if (request == null) {
                System.out.println("  Invalid choice.");
                continue;
            }

            sendMessage(out, request);

            Message response = receiveMessage(in);
            if (response == null) {
                System.out.println("  [!] Server connection closed.");
                break;
            }

            String status = response.getOrDefault("status", "");
            String msg    = response.getOrDefault("message",
                            response.getOrDefault("reason", ""));

            if ("ERROR".equals(status)) {
                System.out.println("  [!] " + msg);
            } else {
                System.out.println(msg);
            }

            if (request.getType() == MessageType.QUIT) break;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Request builders                                                   */
    /* ------------------------------------------------------------------ */

    /**
     * Converts a menu choice string into the corresponding request {@link Message},
     * prompting for any additional parameters needed.
     *
     * @param choice  the single character (or letter) the player typed
     * @param scanner console input for collecting additional parameters
     * @return the constructed {@link Message}, or {@code null} for unrecognised input
     */
    private Message buildRequest(String choice, Scanner scanner) {
        switch (choice.toLowerCase()) {
            case "1":
                return Message.of(MessageType.VIEW_VILLAGE);

            case "2":
                return buildBuildRequest(scanner);

            case "3":
                return buildTrainRequest(scanner);

            case "4":
                return buildUpgradeRequest(scanner);

            case "5":
                return Message.of(MessageType.EXPLORE);

            case "6":
                return buildAttackRequest(scanner);

            case "7":
                return Message.of(MessageType.COLLECT);

            case "8":
                return Message.of(MessageType.VIEW_RANKING);

            case "9":
                return Message.of(MessageType.VIEW_ARMY);

            case "a":
                return Message.of(MessageType.GENERATE_ARMY);

            case "b":
                return Message.of(MessageType.TEST_VILLAGE);

            case "s":
                return Message.of(MessageType.SAVE);

            case "l":
                return Message.of(MessageType.LOAD);

            case "0":
                return Message.of(MessageType.QUIT);

            default:
                return null;
        }
    }

    /**
     * Prompts the player to select a building type and returns a BUILD request.
     *
     * @param scanner console input
     * @return the BUILD message
     */
    private Message buildBuildRequest(Scanner scanner) {
        System.out.println("  BUILD MENU");
        System.out.println("  1. Farm             (50G / 30I / 20L)");
        System.out.println("  2. Gold Mine        (100G / 50I / 50L)");
        System.out.println("  3. Iron Mine        (80G / 60I / 40L)");
        System.out.println("  4. Lumber Mill      (30G / 10I / 60L)");
        System.out.println("  5. Archer Tower     (60G / 40I / 30L)");
        System.out.println("  6. Cannon           (100G / 80I / 40L)");
        System.out.println("  7. Village Hall     (200G / 100I / 100L)");
        System.out.print("  Choice: ");
        String c = scanner.nextLine().trim();
        System.out.println();
        String type = mapBuildChoice(c);
        if (type == null) return Message.of(MessageType.VIEW_VILLAGE); // no-op fallback
        return new Message.Builder(MessageType.BUILD).put("buildingType", type).build();
    }

    /**
     * Prompts the player to select a unit type and returns a TRAIN request.
     *
     * @param scanner console input
     * @return the TRAIN message
     */
    private Message buildTrainRequest(Scanner scanner) {
        System.out.println("  TRAIN MENU");
        System.out.println("  1. Soldier    (20G / 10I / 5L)");
        System.out.println("  2. Archer     (25G / 5I / 15L)");
        System.out.println("  3. Knight     (50G / 30I / 10L)");
        System.out.println("  4. Catapult   (80G / 60I / 50L)");
        System.out.println("  5. Gold Miner (free)");
        System.out.println("  6. Iron Miner (free)");
        System.out.println("  7. Lumberman  (free)");
        System.out.print("  Choice: ");
        String c = scanner.nextLine().trim();
        String type = mapTrainChoice(c);
        if (type == null) return Message.of(MessageType.VIEW_VILLAGE);

        boolean addToArmy = false;
        if (isFighterChoice(c)) {
            System.out.print("  Add to army? (y/n): ");
            addToArmy = "y".equalsIgnoreCase(scanner.nextLine().trim());
        }
        System.out.println();
        return new Message.Builder(MessageType.TRAIN)
                .put("unitType", type)
                .put("addToArmy", String.valueOf(addToArmy))
                .build();
    }

    /**
     * Prompts the player to enter a building index and returns an UPGRADE request.
     *
     * @param scanner console input
     * @return the UPGRADE message
     */
    private Message buildUpgradeRequest(Scanner scanner) {
        System.out.print("  Enter building index to upgrade (1-based): ");
        String idx = scanner.nextLine().trim();
        System.out.println();
        return new Message.Builder(MessageType.UPGRADE)
                .put("buildingIndex", idx)
                .build();
    }

    /**
     * Prompts the player to choose an attack target and returns an ATTACK request.
     *
     * @param scanner console input
     * @return the ATTACK message
     */
    private Message buildAttackRequest(Scanner scanner) {
        System.out.print("  Enter target index (1-based, 0=cancel): ");
        String idx = scanner.nextLine().trim();
        System.out.println();
        if ("0".equals(idx)) return Message.of(MessageType.VIEW_VILLAGE);
        return new Message.Builder(MessageType.ATTACK)
                .put("targetIndex", idx)
                .build();
    }

    /* ------------------------------------------------------------------ */
    /*  Mapping helpers                                                    */
    /* ------------------------------------------------------------------ */

    private String mapBuildChoice(String c) {
        switch (c) {
            case "1": return "Farm";
            case "2": return "GoldMine";
            case "3": return "IronMine";
            case "4": return "LumberMill";
            case "5": return "ArcherTower";
            case "6": return "Cannon";
            case "7": return "VillageHall";
            default:  return null;
        }
    }

    private String mapTrainChoice(String c) {
        switch (c) {
            case "1": return "Soldier";
            case "2": return "Archer";
            case "3": return "Knight";
            case "4": return "Catapult";
            case "5": return "GoldMiner";
            case "6": return "IronMiner";
            case "7": return "Lumberman";
            default:  return null;
        }
    }

    private boolean isFighterChoice(String c) {
        switch (c) {
            case "1": case "2": case "3": case "4": return true;
            default: return false;
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Menu rendering                                                     */
    /* ------------------------------------------------------------------ */

    /** Prints the main game menu to the console. */
    private void printMenu() {
        System.out.println();
        System.out.println("  MAIN MENU");
        System.out.println("  ---------");
        System.out.println("  1. View village status");
        System.out.println("  2. Build new building");
        System.out.println("  3. Train unit");
        System.out.println("  4. Upgrade building");
        System.out.println("  5. Explore villages to attack");
        System.out.println("  6. Attack a village");
        System.out.println("  7. Collect resources");
        System.out.println("  8. View ranking / score");
        System.out.println("  9. View army");
        System.out.println("  a. Generate compatible army (NEW)");
        System.out.println("  b. Test village defences (NEW)");
        System.out.println("  s. Save village");
        System.out.println("  l. Load village");
        System.out.println("  0. Quit");
        System.out.print("  Choice: ");
    }

    /* ------------------------------------------------------------------ */
    /*  I/O helpers                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Serializes and sends a message to the server (one JSON line).
     *
     * @param out     server output writer
     * @param message the message to send
     */
    private void sendMessage(PrintWriter out, Message message) {
        out.println(JsonHelper.toJson(message));
    }

    /**
     * Reads one line from the server and parses it as a {@link Message}.
     *
     * @param in server input reader
     * @return the parsed response, or {@code null} if the connection was closed
     * @throws IOException on I/O error
     */
    private Message receiveMessage(BufferedReader in) throws IOException {
        String line = in.readLine();
        if (line == null) return null;
        try {
            return JsonHelper.fromJson(line);
        } catch (IllegalArgumentException e) {
            System.err.println("  [!] Malformed server response: " + e.getMessage());
            return Message.error("Malformed response from server");
        }
    }
}
