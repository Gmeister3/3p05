package game;

// Simulates an in-game wall clock tracking elapsed game time in ticks.
public class WallClock {

    private int currentTime;

    public WallClock() {
        this.currentTime = 0;
    }

    public void tick() {
        currentTime++;
    }

    public void advance(int ticks) {
        if (ticks > 0) {
            currentTime += ticks;
        }
    }

    public int getTime() {
        return currentTime;
    }

    public boolean checkTime(int threshold) {
        return currentTime >= threshold;
    }

    @Override
    public String toString() {
        return "WallClock[tick=" + currentTime + "]";
    }
}
