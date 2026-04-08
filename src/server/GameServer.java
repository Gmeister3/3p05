package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TCP server that accepts multiple simultaneous client connections and dispatches
 * game requests using a combination of per-client threads and a shared task pool.
 *
 * <h2>Socket choice – TCP</h2>
 * <p>TCP was selected over UDP because the game requires <em>reliable, ordered</em>
 * message delivery.  Each player command (BUILD, TRAIN, ATTACK, …) must arrive
 * exactly once and in the correct sequence; a lost or duplicated packet would
 * corrupt the game state.  TCP provides this guarantee without the need for an
 * application-layer retransmission protocol.</p>
 *
 * <h2>Threading model</h2>
 * <ol>
 *   <li><b>Accept thread</b> – the main server loop calls {@link ServerSocket#accept()}
 *       and spawns a new {@link ClientHandler} thread for every incoming connection.</li>
 *   <li><b>Per-client threads</b> – each {@link ClientHandler} owns its dedicated
 *       session ({@link GameSession}) and handles the protocol I/O loop independently,
 *       so clients do not block each other.</li>
 *   <li><b>Shared task pool</b> – a fixed-size {@link ExecutorService} with
 *       {@value #POOL_SIZE} threads is shared across all sessions.  Heavy operations
 *       (generate village, generate army, attack resolution, village test) are submitted
 *       as {@link Callable} tasks.  This parallelises those CPU-intensive operations
 *       and prevents any single client from monopolising the CPU.</li>
 * </ol>
 *
 * <h2>Multi-client support</h2>
 * <p>The server supports multiple simultaneous clients.  Each client has its own
 * isolated {@link GameSession}; state is never shared between sessions, so no
 * additional synchronisation is needed beyond the thread-safe {@link PlayerDatabase}.</p>
 */
public class GameServer {

    /** Default port the server listens on. */
    public static final int DEFAULT_PORT = 9090;

    /**
     * Number of threads in the shared task pool.
     * Set to 4 so that up to four heavy operations run in parallel across all sessions.
     */
    private static final int POOL_SIZE = 4;

    /** The TCP server socket. */
    private final ServerSocket serverSocket;

    /** Shared user/password store. */
    private final PlayerDatabase database;

    /**
     * Shared fixed-size thread pool for CPU-intensive game operations
     * (generate village, generate army, attacks, village tests).
     */
    private final ExecutorService taskPool;

    /**
     * Thread pool used to run per-client {@link ClientHandler} threads.
     * Unbounded (cached) so that any number of clients can connect simultaneously.
     */
    private final ExecutorService clientPool;

    /** Counter used to assign unique connection IDs. */
    private final AtomicInteger connectionCounter = new AtomicInteger(1);

    /** Whether the server is currently accepting connections. */
    private volatile boolean running = true;

    /* ------------------------------------------------------------------ */
    /*  Constructor                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Creates the server socket bound to the given port and initialises thread pools.
     *
     * @param port TCP port to listen on
     * @throws IOException if the server socket cannot be created
     */
    public GameServer(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.database     = new PlayerDatabase();
        this.taskPool     = Executors.newFixedThreadPool(POOL_SIZE);
        this.clientPool   = Executors.newCachedThreadPool();
        System.out.println("[Server] Village War Strategy Game server started on port " + port);
        System.out.println("[Server] Task pool size: " + POOL_SIZE + " threads");
        System.out.println("[Server] Default accounts: alice/alice123, bob/bob456, admin/admin");
    }

    /* ------------------------------------------------------------------ */
    /*  Accept loop                                                        */
    /* ------------------------------------------------------------------ */

    /**
     * Runs the accept loop.  Blocks until {@link #stop()} is called or the
     * server socket is closed.
     *
     * <p>Each accepted connection is handed to a new {@link ClientHandler} and
     * submitted to the {@code clientPool} so the accept thread is never blocked
     * waiting for a client to finish.</p>
     */
    public void run() {
        System.out.println("[Server] Waiting for connections...");
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                int id = connectionCounter.getAndIncrement();
                ClientHandler handler = new ClientHandler(clientSocket, database, taskPool, id);
                clientPool.submit(handler);
            } catch (IOException e) {
                if (running) {
                    System.err.println("[Server] Accept error: " + e.getMessage());
                }
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Lifecycle                                                          */
    /* ------------------------------------------------------------------ */

    /**
     * Stops the server by closing the server socket and shutting down thread pools.
     */
    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException ignored) { }
        clientPool.shutdown();
        taskPool.shutdown();
        System.out.println("[Server] Server stopped.");
    }
}
