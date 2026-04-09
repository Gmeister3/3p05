import controller.GameController;
import game.*;
import exceptions.*;
import gui.GraphicalInterface;

import java.util.*;
import java.io.*;

// Entry point for the Village War Strategy Game (COSC 3P91 Assignment 3).
// Pattern: MVC – wiring point. Main creates the Model (GameEngine, Player), the View
// (GraphicalInterface), and the Controller (GameController), then enters the game loop.
// All game logic resides in GameController; all rendering resides in GraphicalInterface;
// all state lives in the game/ and gameelements/ packages.
public class Main {

    public static void main(String[] args) {
        /* ---- I/O ---- */
        Scanner scanner = new Scanner(new BufferedReader(new InputStreamReader(System.in)));

        /* ---- View (MVC) ---- */
        GraphicalInterface view = new GraphicalInterface();

        /* ---- Model (MVC) ---- */
        GameEngine engine = new GameEngine();

        printWelcomeBanner();

        System.out.print("  Enter your village name: ");
        String villageName = scanner.nextLine().trim();
        if (villageName.isEmpty()) villageName = "MyVillage";

        Player player = new Player(1, villageName);
        engine.registerPlayer(player);
        engine.generateNpcVillages(5);
        engine.setRunning(true);

        System.out.println("\n  Village '" + villageName + "' created!");
        System.out.println("  Starting resources: 500 Gold, 300 Iron, 400 Lumber");
        System.out.println("  Buildings: 1 Village Hall + 1 Farm");

        /* ---- Controller (MVC) ---- */
        GameController controller = new GameController(engine, player, view, scanner);

        /* ---- Main game loop ---- */
        while (engine.isRunning()) {
            engine.tick();
            view.renderMenu();

            String input = scanner.nextLine().trim();
            System.out.println();

            try {
                controller.handleMenuChoice(input);
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

    /** Prints the ASCII welcome banner using an anonymous Runnable. */
    private static void printWelcomeBanner() {
        Runnable banner = new Runnable() {
            @Override
            public void run() {
                System.out.println("============================================================");
                System.out.println("       VILLAGE WAR STRATEGY GAME  (COSC 3P91 A3)           ");
                System.out.println("============================================================");
                System.out.println("  Build your village, train your army, and conquer enemies! ");
                System.out.println("============================================================");
                System.out.println();
            }
        };
        banner.run();
    }
}

