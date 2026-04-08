import client.GameClient;

/**
 * Entry point for the Village War Strategy Game <b>client</b> (COSC 3P91 Assignment 4).
 *
 * <p>Connects to the running {@link server.GameServer} and presents the interactive
 * console menu to the player.</p>
 *
 * <h2>Compilation</h2>
 * <pre>
 *   javac -d out $(find src -name "*.java")
 * </pre>
 *
 * <h2>Running</h2>
 * <pre>
 *   # Start server first (in one terminal):
 *   java -cp out ServerMain
 *
 *   # Start client (in another terminal):
 *   java -cp out ClientMain                    # defaults: localhost:9090
 *   java -cp out ClientMain 192.168.1.5        # custom host, default port
 *   java -cp out ClientMain 192.168.1.5 8080   # custom host and port
 * </pre>
 *
 * <h2>Default accounts (pre-seeded on the server)</h2>
 * <pre>
 *   alice / alice123
 *   bob   / bob456
 *   admin / admin
 * </pre>
 */
public class ClientMain {

    /**
     * Parses optional {@code host} and {@code port} arguments and launches the client.
     *
     * @param args optional: {@code [host [port]]}
     */
    public static void main(String[] args) {
        String host = GameClient.DEFAULT_HOST;
        int    port = GameClient.DEFAULT_PORT;

        if (args.length >= 1) {
            host = args[0];
        }
        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[1] + " – using default " + port);
            }
        }

        new GameClient(host, port).run();
    }
}
