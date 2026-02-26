package game;

import gameelements.*;

import java.util.*;
import java.util.stream.*;

/**
 * Collects resources from all worker units and resource-producing buildings in a village.
 * <p>
 * The {@code CollectResources} action iterates over all {@link Peasant} habitants and
 * production buildings ({@link GoldMine}, {@link IronMine}, {@link LumberMill}),
 * accumulates their output, and deposits it into the village resource stores.
 * </p>
 * <p>
 * Demonstrates lambda expressions, stream {@code forEach}, and method references.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class CollectResources {

    /**
     * Executes the resource collection for the given village.
     * <p>
     * For each {@link Peasant} the {@link Peasant#work()} method is called and the
     * returned {@link Resource} is credited to the village. Building production is
     * also credited based on their {@code *Prod} rates.
     * </p>
     *
     * @param village the {@link Village} from which to collect resources
     * @return a summary string describing the resources collected
     */
    public String collect(Village village) {
        double totalGold   = 0;
        double totalIron   = 0;
        double totalLumber = 0;

        // --- Peasant production using streams and method references ---
        List<Peasant> peasants = village.getPeasants();

        // Collect resources from peasants using stream + forEach + lambda
        List<Resource> produced = peasants.stream()
                .map(Peasant::work)
                .collect(Collectors.toList());

        for (Resource r : produced) {
            if (r instanceof Gold)   totalGold   += r.getQuantity();
            if (r instanceof Iron)   totalIron   += r.getQuantity();
            if (r instanceof Lumber) totalLumber += r.getQuantity();
        }

        // --- Building production using streams ---
        List<Building> buildings = village.getBuildingsMutable();

        totalGold += buildings.stream()
                .filter(b -> b instanceof GoldMine)
                .mapToDouble(b -> ((GoldMine) b).getGoldProd())
                .sum();

        totalIron += buildings.stream()
                .filter(b -> b instanceof IronMine)
                .mapToDouble(b -> ((IronMine) b).getIronProd())
                .sum();

        totalLumber += buildings.stream()
                .filter(b -> b instanceof LumberMill)
                .mapToDouble(b -> ((LumberMill) b).getLumbProd())
                .sum();

        // Deposit resources into village
        village.getGold().add(totalGold);
        village.getIron().add(totalIron);
        village.getLumber().add(totalLumber);

        return String.format("Collected: +%.1f Gold, +%.1f Iron, +%.1f Lumber",
                totalGold, totalIron, totalLumber);
    }
}
