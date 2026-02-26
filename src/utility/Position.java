package utility;

/**
 * Represents a 2-dimensional coordinate on the game map.
 * <p>
 * Positions are used to locate villages, regions, and other game entities
 * on the {@link GameMap}.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Position {

    /** The horizontal coordinate. */
    private int x;

    /** The vertical coordinate. */
    private int y;

    /**
     * Constructs a Position at the origin (0, 0).
     */
    public Position() {
        this.x = 0;
        this.y = 0;
    }

    /**
     * Constructs a Position with the given coordinates.
     *
     * @param x the horizontal coordinate
     * @param y the vertical coordinate
     */
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the horizontal coordinate.
     *
     * @return the x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the horizontal coordinate.
     *
     * @param x the new x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Returns the vertical coordinate.
     *
     * @return the y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the vertical coordinate.
     *
     * @param y the new y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Calculates the Euclidean distance between this position and another position.
     *
     * @param other the other position
     * @return the Euclidean distance as a double
     */
    public double distanceTo(Position other) {
        int dx = this.x - other.x;
        int dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns a string representation of this position in the form {@code (x, y)}.
     *
     * @return string representation
     */
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    /**
     * Checks equality based on x and y coordinates.
     *
     * @param obj the object to compare
     * @return {@code true} if the positions have the same coordinates
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position other = (Position) obj;
        return this.x == other.x && this.y == other.y;
    }

    /**
     * Returns a hash code based on the coordinates.
     *
     * @return hash code
     */
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
}
