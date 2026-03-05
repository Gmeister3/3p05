package game;

// Represents a human player with a village, army, and score.
public class Player {

    private final int playerID;
    private Village village;
    private Army army;
    private int score;
    private int attacksWon;
    private int attacksTotal;

    public Player(int playerID, String villageName) {
        this.playerID     = playerID;
        this.village      = new Village(villageName);
        this.army         = new Army();
        this.score        = 0;
        this.attacksWon   = 0;
        this.attacksTotal = 0;
    }

    public void notify(Village target) {
        System.out.println("[Player " + playerID + "] Notification: Village '"
                + target.getName() + "' state has changed.");
        System.out.println("  Current defence score: " + target.getDefenceScore());
    }

    public void recordAttack(int points, boolean success) {
        score += points;
        attacksTotal++;
        if (success) attacksWon++;
    }

    public int getPlayerID() { return playerID; }
    public Village getVillage() { return village; }
    public Army getArmy() { return army; }
    public int getScore() { return score; }
    public int getAttacksWon() { return attacksWon; }
    public int getAttacksTotal() { return attacksTotal; }

    public String getRankingSummary() {
        return String.format("Player %d | Score: %d | Attacks: %d/%d won",
                playerID, score, attacksWon, attacksTotal);
    }
}
