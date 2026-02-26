package game;

/**
 * Simulates an in-game wall clock used to track elapsed game time in ticks.
 * <p>
 * Game actions that are time-gated (such as guard periods) use the {@code WallClock}
 * to verify that sufficient time has passed before allowing an operation.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class WallClock {

    /** The current game time expressed as an integer tick count. */
    private int currentTime;

    /**
     * Constructs a WallClock starting at tick 0.
     */
    public WallClock() {
        this.currentTime = 0;
    }

    /**
     * Advances the clock by one tick.
     */
    public void tick() {
        currentTime++;
    }

    /**
     * Advances the clock by the specified number of ticks.
     *
     * @param ticks the number of ticks to advance (must be positive)
     */
    public void advance(int ticks) {
        if (ticks > 0) {
            currentTime += ticks;
        }
    }

    /**
     * Returns the current game tick count.
     *
     * @return current time in ticks
     */
    public int getTime() {
        return currentTime;
    }

    /**
     * Checks whether the current time has reached or exceeded the specified threshold.
     *
     * @param threshold the tick count to compare against
     * @return {@code true} if {@code currentTime >= threshold}
     */
    public boolean checkTime(int threshold) {
        return currentTime >= threshold;
    }

    /**
     * Returns a string representation of the clock.
     *
     * @return string showing the current tick
     */
    @Override
    public String toString() {
        return "WallClock[tick=" + currentTime + "]";
    }
}
