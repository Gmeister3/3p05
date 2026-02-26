package utility;

import game.Army;
import game.Village;

import java.util.Random;

/**
 * Judges combat outcomes between an attacking {@link Army} and a defending {@link Village}.
 * <p>
 * The {@code Arbitrer} computes the result of a battle using a combination of the
 * army's attack score, the village's defence score, and a random factor. Loot is
 * proportional to the success margin and capped at 30% of the defender's resources.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Arbitrer {

    /** Random number generator for combat variance. */
    private final Random random;

    /** Loot cap: maximum fraction of defender's resources that can be taken. */
    private static final double LOOT_CAP = 0.30;

    /**
     * Constructs an {@code Arbitrer} with a default random number generator.
     */
    public Arbitrer() {
        this.random = new Random();
    }

    /**
     * Judges the outcome of an attack.
     * <p>
     * Attack success is determined by:
     * <pre>
     *   attackScore + randomFactor(0..50) &gt; defenceScore
     * </pre>
     * If successful, loot is calculated proportionally to the victory margin,
     * capped at {@value #LOOT_CAP} of the defender's resources.
     * </p>
     *
     * @param attacker the attacking {@link Army}
     * @param defender the defending {@link Village}
     * @return an {@link AttackOutcome} describing the result and any loot gained
     */
    public AttackOutcome judgeAttack(Army attacker, Village defender) {
        double attackScore = attacker.getAttackScore();
        double defenceScore = defender.getDefenceScore();

        // Random variance in the range [0, 50]
        double randomFactor = random.nextDouble() * 50.0;
        double effectiveAttack = attackScore + randomFactor;

        boolean success = effectiveAttack > defenceScore;

        if (!success) {
            return new AttackOutcome(false, 0, 0, 0,
                    String.format("Attack FAILED! Effective attack (%.1f) could not overcome defence (%.1f).",
                            effectiveAttack, defenceScore));
        }

        // Calculate loot proportional to the margin of victory, capped at LOOT_CAP
        double margin = effectiveAttack - defenceScore;
        double lootFraction = Math.min(LOOT_CAP, margin / (defenceScore + 1) * LOOT_CAP * 3.0);

        double goldLooted  = defender.getGold().getQuantity()   * lootFraction;
        double ironLooted  = defender.getIron().getQuantity()   * lootFraction;
        double lumberLooted = defender.getLumber().getQuantity() * lootFraction;

        // Remove resources from defender
        defender.getGold().subtract(goldLooted);
        defender.getIron().subtract(ironLooted);
        defender.getLumber().subtract(lumberLooted);

        String msg = String.format(
                "Attack SUCCEEDED! (attack=%.1f, defence=%.1f) Looted: Gold=%.1f, Iron=%.1f, Lumber=%.1f",
                effectiveAttack, defenceScore, goldLooted, ironLooted, lumberLooted);

        return new AttackOutcome(true, goldLooted, ironLooted, lumberLooted, msg);
    }
}
