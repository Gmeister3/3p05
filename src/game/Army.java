package game;

import gameelements.*;
import exceptions.*;
import utility.AttackOutcome;
import utility.Arbitrer;

import java.util.*;
import java.util.stream.*;

/**
 * Represents the player's attack force composed of {@link Fighter} units.
 * <p>
 * An {@code Army} maintains a list of fighters and computes an aggregate attack score
 * used by the {@link Arbitrer} when resolving combat against a defending {@link Village}.
 * The army is capped at {@value #MAX_ARMY_SIZE} units.
 * </p>
 * <p>
 * Demonstrates generics ({@code List<Fighter>}), wildcards, streams, and lambdas.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Army {

    /** Maximum number of fighters allowed in the army. */
    public static final int MAX_ARMY_SIZE = 10;

    /** Aggregate damage value (sum of all fighter damage). */
    private double damage;

    /** The list of fighters in this army. */
    private final List<Fighter> fighters;

    /**
     * Constructs an empty Army.
     */
    public Army() {
        this.fighters = new ArrayList<>();
        this.damage   = 0;
    }

    /**
     * Adds a fighter to the army.
     *
     * @param fighter the {@link Fighter} to add
     * @throws InvalidOperationException if the army is already at maximum capacity
     */
    public void addFighter(Fighter fighter) throws InvalidOperationException {
        if (fighters.size() >= MAX_ARMY_SIZE) {
            throw new InvalidOperationException("addFighter",
                    "Army is at maximum capacity of " + MAX_ARMY_SIZE + " units.");
        }
        fighters.add(fighter);
        recalculateDamage();
    }

    /**
     * Removes a fighter from the army by index.
     *
     * @param index zero-based index of the fighter to remove
     * @throws InvalidOperationException if the index is out of range
     */
    public void removeFighter(int index) throws InvalidOperationException {
        if (index < 0 || index >= fighters.size()) {
            throw new InvalidOperationException("removeFighter",
                    "Invalid fighter index: " + index);
        }
        fighters.remove(index);
        recalculateDamage();
    }

    /**
     * Attacks the specified village and returns the outcome.
     * <p>
     * Combat is resolved by the {@link Arbitrer}. This method also clears defeated
     * fighters from the army after the engagement.
     * </p>
     *
     * @param defender the {@link Village} to attack
     * @return an {@link AttackOutcome} describing the result and loot
     * @throws InvalidOperationException if the army is empty
     */
    public AttackOutcome attack(Village defender) throws InvalidOperationException {
        if (fighters.isEmpty()) {
            throw new InvalidOperationException("attack", "Cannot attack with an empty army.");
        }

        Arbitrer arbitrer = new Arbitrer();
        AttackOutcome outcome = arbitrer.judgeAttack(this, defender);

        // Remove defeated fighters after battle (simulate casualties on failed attacks)
        if (!outcome.isSuccess()) {
            // On failure, lose ~30% of fighters (random)
            Random rand = new Random();
            fighters.removeIf(f -> rand.nextDouble() < 0.30);
        }

        recalculateDamage();
        return outcome;
    }

    /**
     * Recalculates the aggregate damage value from the current fighter list.
     * Uses a method reference with {@code mapToDouble} stream.
     */
    private void recalculateDamage() {
        damage = fighters.stream()
                .mapToDouble(Fighter::damage)
                .sum();
    }

    /**
     * Returns the aggregate damage of the entire army.
     *
     * @return total damage
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Returns the army's overall attack score (damage × fighter count factor).
     * Used by the {@link Arbitrer} for combat resolution.
     *
     * @return the attack score
     */
    public double getAttackScore() {
        if (fighters.isEmpty()) return 0;
        // Score = total damage * sqrt(number of fighters) for squad synergy
        return damage * Math.sqrt(fighters.size());
    }

    /**
     * Returns an unmodifiable view of the fighter list.
     *
     * @return list of fighters
     */
    public List<Fighter> getFighters() {
        return Collections.unmodifiableList(fighters);
    }

    /**
     * Returns the number of fighters in this army.
     *
     * @return army size
     */
    public int size() {
        return fighters.size();
    }

    /**
     * Returns whether the army has no fighters.
     *
     * @return {@code true} if the army is empty
     */
    public boolean isEmpty() {
        return fighters.isEmpty();
    }

    /**
     * Returns a summary of the army's fighters using streams and collectors.
     *
     * @return formatted multi-line summary string
     */
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

    /**
     * Accepts a wildcard list of fighters and adds all of them to this army.
     * <p>Demonstrates use of bounded wildcard: {@code List<? extends Fighter>}.</p>
     *
     * @param newFighters a list of fighters to add (uses upper-bounded wildcard)
     * @throws InvalidOperationException if any addition causes the army to exceed its limit
     */
    public void addAll(List<? extends Fighter> newFighters) throws InvalidOperationException {
        for (Fighter f : newFighters) {
            addFighter(f);
        }
    }
}
