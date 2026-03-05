package utility;

import game.Army;
import game.Village;

import java.util.Random;

// Judges combat outcomes between an attacking Army and a defending Village.
// Success: attackScore + random(0..50) > defenceScore.
// Loot is proportional to victory margin, capped at 30% of defender's resources.
public class Arbitrer {

    private final Random random;

    // max fraction of defender's resources that can be looted per attack
    private static final double LOOT_CAP = 0.30;

    public Arbitrer() {
        this.random = new Random();
    }

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
