package utility;

// Encapsulates the result of a combat engagement: success flag, loot amounts, and summary message.
public class AttackOutcome {

    private final boolean success;
    private final double newGold;
    private final double newIron;
    private final double newLumber;
    private final String message;

    public AttackOutcome(boolean success, double newGold, double newIron,
                         double newLumber, String message) {
        this.success = success;
        this.newGold = newGold;
        this.newIron = newIron;
        this.newLumber = newLumber;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public double getNewGold() {
        return newGold;
    }

    public double getNewIron() {
        return newIron;
    }

    public double getNewLumber() {
        return newLumber;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return String.format("AttackOutcome{success=%b, gold=%.1f, iron=%.1f, lumber=%.1f}",
                success, newGold, newIron, newLumber);
    }
}
