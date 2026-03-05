package gui;

import game.*;
import gameelements.*;

import java.util.*;
import java.util.stream.*;

// Console-based graphical interface; all rendering goes to System.out.
public class GraphicalInterface {

    private static final int LINE_WIDTH = 60;
    private static final String SEPARATOR = "=".repeat(LINE_WIDTH);

    public GraphicalInterface() { }

    public void renderVillage(Village village) {
        System.out.println(SEPARATOR);
        System.out.printf("  VILLAGE: %-40s%n", village.getName());
        System.out.println(SEPARATOR);

        // Resources block
        System.out.println("  RESOURCES:");
        System.out.printf("    Gold:   %8.1f%n", village.getGold().getQuantity());
        System.out.printf("    Iron:   %8.1f%n", village.getIron().getQuantity());
        System.out.printf("    Lumber: %8.1f%n", village.getLumber().getQuantity());
        System.out.println();

        // Scores
        System.out.printf("  Defence Score : %.1f%n", village.getDefenceScore());
        System.out.printf("  Attack Score  : %.1f%n", village.getAttackScore());
        System.out.println();

        // Buildings
        System.out.println("  BUILDINGS (" + village.getBuildings().size()
                + "/" + Village.MAX_BUILDINGS + "):");
        if (village.getBuildings().isEmpty()) {
            System.out.println("    (none)");
        } else {
            // Use stream + forEach with lambda
            village.getBuildings().stream()
                    .sorted(Comparator.comparingInt(Building::getLevel).reversed())
                    .forEach(b -> System.out.printf("    %-20s Lv%d  HP:%.0f%n",
                            b.getName(), b.getLevel(), b.getHitPoints()));
        }
        System.out.println();

        // Habitants
        System.out.println("  HABITANTS (" + village.getHabitants().size() + "):");
        if (village.getHabitants().isEmpty()) {
            System.out.println("    (none)");
        } else {
            // Group by class name using streams
            Map<String, Long> grouped = village.getHabitants().stream()
                    .collect(Collectors.groupingBy(
                            h -> h.getClass().getSimpleName(),
                            Collectors.counting()));
            grouped.forEach((type, count) ->
                    System.out.printf("    %-20s x%d%n", type, count));
        }
        System.out.println(SEPARATOR);
    }

    public void renderArmy(Army army) {
        System.out.println(SEPARATOR);
        System.out.println("  ARMY STATUS");
        System.out.println(SEPARATOR);
        System.out.printf("  Size: %d / %d%n", army.size(), Army.MAX_ARMY_SIZE);
        System.out.printf("  Total Damage  : %.1f%n", army.getDamage());
        System.out.printf("  Attack Score  : %.1f%n", army.getAttackScore());
        System.out.println();

        if (army.isEmpty()) {
            System.out.println("  (No fighters in army)");
        } else {
            System.out.println("  Fighters:");
            // Use stream grouping + forEach
            Map<String, Long> byType = army.getFighters().stream()
                    .collect(Collectors.groupingBy(
                            f -> f.getClass().getSimpleName(),
                            Collectors.counting()));
            byType.forEach((type, count) ->
                    System.out.printf("    %-20s x%d%n", type, count));
            System.out.println();
            System.out.println("  Individual units:");
            List<Fighter> fighters = army.getFighters();
            for (int i = 0; i < fighters.size(); i++) {
                System.out.printf("    [%2d] %s%n", i + 1, fighters.get(i));
            }
        }
        System.out.println(SEPARATOR);
    }

    public void renderTargets(List<Village> targets) {
        System.out.println(SEPARATOR);
        System.out.println("  AVAILABLE TARGETS");
        System.out.println(SEPARATOR);
        if (targets.isEmpty()) {
            System.out.println("  No targets available.");
        } else {
            for (int i = 0; i < targets.size(); i++) {
                Village v = targets.get(i);
                System.out.printf("  [%d] %-20s  DefScore: %6.1f  Gold: %.0f%n",
                        i + 1, v.getName(), v.getDefenceScore(),
                        v.getGold().getQuantity());
            }
        }
        System.out.println(SEPARATOR);
    }

    public void printSeparator() {
        System.out.println(SEPARATOR);
    }

    public void printBanner(String title) {
        System.out.println(SEPARATOR);
        int pad = (LINE_WIDTH - title.length() - 2) / 2;
        System.out.println(" ".repeat(Math.max(0, pad)) + title);
        System.out.println(SEPARATOR);
    }

    public void renderMenu() {
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
        System.out.println("  0. Quit");
        System.out.print("  Choice: ");
    }
}
