package utility;

import ChallengeDecision.*;
import game.Army;
import game.Village;
import gameelements.Fighter;

import java.util.ArrayList;
import java.util.List;

// Adapter (Object Adapter pattern) that translates the game's own Army and Village
// objects into the API required by the provided ChallengeDecision.Arbitrer.
//
// Pattern: Adapter (Structural)
//   Target interface: AttackOutcome – what the game's Army expects back from a combat
//     resolution call.
//   Adaptee: ChallengeDecision.Arbitrer.challengeDecide – the provided external
//     combat-resolution engine that we must not modify.
//   Adapter: this class – bridges the two incompatible APIs by converting game entities
//     into ChallengeEntitySet objects, delegating to the external arbitrer, and converting
//     the result back.
//
// The adapter is used by Army.attack(Village) to replace the old hand-rolled Arbitrer
// with the provided external engine.
public class ChallengeDecisionAdapter {

    /**
     * Resolves the outcome of an attack using the provided ChallengeDecision engine.
     * Each fighter in the attacking army is mapped to a ChallengeAttack whose attack
     * property is the fighter's damage and whose hit-points are the fighter's current HP.
     * Each building in the defending village is mapped to a ChallengeDefense whose defense
     * property is the building's level times its hit-points (mirroring how
     * Village.getDefenceScore works). The defender's gold, iron, and lumber are each
     * represented as a ChallengeResource so the external engine can compute proportional loot.
     *
     * @param attacker the player's attacking {@link Army}
     * @param defender the NPC {@link Village} being attacked
     * @return an {@link AttackOutcome} with success flag, loot amounts, and a summary message
     */
    public AttackOutcome adapt(Army attacker, Village defender) {

        /* --- Build challenger entity set (attacker) --- */
        List<ChallengeAttack<Double, Double>> attackList = new ArrayList<>();
        for (Fighter f : attacker.getFighters()) {
            ChallengeAttack<Double, Double> ca = new ChallengeAttack<>(f.damage(), f.getHitPoints());
            attackList.add(ca);
        }

        /* --- Build challengee entity set (defender) --- */
        List<ChallengeDefense<Double, Double>> defenseList = new ArrayList<>();
        defender.getBuildings().forEach(b -> {
            double defValue = b.getLevel() * b.getHitPoints();
            ChallengeDefense<Double, Double> cd = new ChallengeDefense<>(defValue, b.getHitPoints());
            defenseList.add(cd);
        });

        /* --- Add defender resources so the engine can calculate loot --- */
        List<ChallengeResource<Double, Double>> resourceList = new ArrayList<>();
        resourceList.add(new ChallengeResource<>(defender.getGold().getQuantity()));
        resourceList.add(new ChallengeResource<>(defender.getIron().getQuantity()));
        resourceList.add(new ChallengeResource<>(defender.getLumber().getQuantity()));

        ChallengeEntitySet<Double, Double> challenger =
                new ChallengeEntitySet<>(attackList, new ArrayList<>(), new ArrayList<>());
        ChallengeEntitySet<Double, Double> challengee =
                new ChallengeEntitySet<>(new ArrayList<>(), defenseList, resourceList);

        /* --- Delegate to the external (unmodifiable) arbitrer --- */
        ChallengeResult result = ChallengeDecision.Arbitrer.challengeDecide(challenger, challengee);

        /* --- Translate result back to the game's AttackOutcome --- */
        if (!result.getChallengeWon()) {
            return new AttackOutcome(false, 0, 0, 0,
                    String.format("Attack FAILED! Defence score (%.1f) repelled the assault.",
                            defender.getDefenceScore()));
        }

        // Loot list order: gold, iron, lumber (matching the order we added resources)
        List<ChallengeResource<Double, Double>> loot = result.getLoot();
        double goldLooted   = loot.size() > 0 ? loot.get(0).getProperty() : 0;
        double ironLooted   = loot.size() > 1 ? loot.get(1).getProperty() : 0;
        double lumberLooted = loot.size() > 2 ? loot.get(2).getProperty() : 0;

        // Deduct looted resources from the defender village
        defender.getGold().subtract(goldLooted);
        defender.getIron().subtract(ironLooted);
        defender.getLumber().subtract(lumberLooted);

        String msg = String.format(
                "Attack SUCCEEDED! Looted: Gold=%.1f, Iron=%.1f, Lumber=%.1f",
                goldLooted, ironLooted, lumberLooted);

        return new AttackOutcome(true, goldLooted, ironLooted, lumberLooted, msg);
    }
}
