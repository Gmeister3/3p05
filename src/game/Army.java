package game;

import gameelements.*;
import exceptions.*;
import utility.AttackOutcome;
import utility.Arbitrer;
import utility.ChallengeDecisionAdapter;

import java.util.*;
import java.util.stream.*;

// Represents the player's attack force composed of Fighter units.
// Capped at MAX_ARMY_SIZE units; computes an aggregate attack score for combat.
public class Army {

    public static final int MAX_ARMY_SIZE = 10;

    private double damage;
    private final List<Fighter> fighters;

    public Army() {
        this.fighters = new ArrayList<>();
        this.damage   = 0;
    }

    public void addFighter(Fighter fighter) throws InvalidOperationException {
        if (fighters.size() >= MAX_ARMY_SIZE) {
            throw new InvalidOperationException("addFighter",
                    "Army is at maximum capacity of " + MAX_ARMY_SIZE + " units.");
        }
        fighters.add(fighter);
        recalculateDamage();
    }

    public void removeFighter(int index) throws InvalidOperationException {
        if (index < 0 || index >= fighters.size()) {
            throw new InvalidOperationException("removeFighter",
                    "Invalid fighter index: " + index);
        }
        fighters.remove(index);
        recalculateDamage();
    }

    /**
     * Launches an attack against the given defending village.
     * Uses the ChallengeDecisionAdapter (Adapter pattern) to delegate combat resolution
     * to the provided ChallengeDecision.Arbitrer engine without modifying those external
     * classes.
     *
     * @param defender the {@link Village} to attack
     * @return an {@link AttackOutcome} describing the result
     * @throws InvalidOperationException if the army is empty
     */
    public AttackOutcome attack(Village defender) throws InvalidOperationException {
        if (fighters.isEmpty()) {
            throw new InvalidOperationException("attack", "Cannot attack with an empty army.");
        }

        // Use the Adapter to call the external ChallengeDecision engine
        ChallengeDecisionAdapter adapter = new ChallengeDecisionAdapter();
        AttackOutcome outcome = adapter.adapt(this, defender);

        // Remove defeated fighters after battle (simulate casualties on failed attacks)
        if (!outcome.isSuccess()) {
            // On failure, lose ~30% of fighters (random)
            Random rand = new Random();
            fighters.removeIf(f -> rand.nextDouble() < 0.30);
        }

        recalculateDamage();
        return outcome;
    }

    // Uses stream mapToDouble with method reference to sum fighter damage.
    private void recalculateDamage() {
        damage = fighters.stream()
                .mapToDouble(Fighter::damage)
                .sum();
    }

    public double getDamage() {
        return damage;
    }

    // Attack score = total damage * sqrt(fighters) for squad synergy.
    public double getAttackScore() {
        if (fighters.isEmpty()) return 0;
        // Score = total damage * sqrt(number of fighters) for squad synergy
        return damage * Math.sqrt(fighters.size());
    }

    public List<Fighter> getFighters() {
        return Collections.unmodifiableList(fighters);
    }

    public int size() {
        return fighters.size();
    }

    public boolean isEmpty() {
        return fighters.isEmpty();
    }

    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Army (").append(fighters.size()).append("/")
          .append(MAX_ARMY_SIZE).append(") ===\n");
        sb.append(String.format("  Total Damage: %.1f | Attack Score: %.1f%n",
                damage, getAttackScore()));

        // Use stream + collect to build a count-by-type map
        Map<String, Long> countByType = fighters.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getClass().getSimpleName(), Collectors.counting()));

        countByType.forEach((type, count) ->
                sb.append("  ").append(type).append(": ").append(count).append("\n"));

        // List individual fighters
        for (int i = 0; i < fighters.size(); i++) {
            sb.append("  [").append(i + 1).append("] ").append(fighters.get(i)).append("\n");
        }
        return sb.toString();
    }

    // Adds all fighters from a bounded wildcard list (List<? extends Fighter>).
    public void addAll(List<? extends Fighter> newFighters) throws InvalidOperationException {
        for (Fighter f : newFighters) {
            addFighter(f);
        }
    }
}
