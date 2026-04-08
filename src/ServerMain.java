import server.GameServer;

import java.io.IOException;

/**
 * Entry point for the Village War Strategy Game <b>server</b> (COSC 3P91 Assignment 4).
 *
 * <p>Starts a {@link GameServer} on the default port ({@value GameServer#DEFAULT_PORT})
 * or on a custom port supplied as a command-line argument.</p>
 *
 * <h2>Compilation</h2>
 * <pre>
 *   javac -d out $(find src -name "*.java")
 * </pre>
 *
 * <h2>Running the server</h2>
 * <pre>
 *   java -cp out ServerMain           # uses default port 9090
 *   java -cp out ServerMain 8080      # uses custom port 8080
 * </pre>
 *
 * <h2>Running the client</h2>
 * <pre>
 *   java -cp out ClientMain                      # connects to localhost:9090
 *   java -cp out ClientMain localhost 8080       # connects to custom host/port
 * </pre>
 */
public class ServerMain {

    /**
     * Main entry point – parses optional port argument and starts the server.
     *
     * @param args optional: {@code [port]}
     */
    public static void main(String[] args) {
        int port = GameServer.DEFAULT_PORT;
        if (args.length >= 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[0] + " – using default " + port);
            }
        }

        try {
            GameServer server = new GameServer(port);

            // Register a JVM shutdown hook so Ctrl-C cleanly stops the server
            final GameServer finalServer = server;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> finalServer.stop()));

            server.run(); // blocks until stopped
        } catch (IOException e) {
            System.err.println("[Server] Failed to start: " + e.getMessage());
            System.exit(1);
        }
    }
}
