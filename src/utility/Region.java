package utility;

/**
 * Represents a rectangular region on the game map defined by its top-left and bottom-right corners.
 * <p>
 * A Region is used to partition the {@link GameMap} into logical areas, allowing
 * villages and resources to be located within specific bounds.
 * </p>
 *
 * @author COSC 3P91 Assignment 2
 * @version 1.0
 */
public class Region {

    /**
     * Array holding the bounding positions: index 0 = top-left, index 1 = bottom-right.
     */
    private final Position[] topLeftBottomRight;

    /**
     * Constructs a Region from two corner positions.
     *
     * @param topLeft     the top-left corner of the region
     * @param bottomRight the bottom-right corner of the region
     */
    public Region(Position topLeft, Position bottomRight) {
        this.topLeftBottomRight = new Position[]{topLeft, bottomRight};
    }

    /**
     * Constructs a Region defined by coordinate values.
     *
     * @param x1 the x-coordinate of the top-left corner
     * @param y1 the y-coordinate of the top-left corner
     * @param x2 the x-coordinate of the bottom-right corner
     * @param y2 the y-coordinate of the bottom-right corner
     */
    public Region(int x1, int y1, int x2, int y2) {
        this.topLeftBottomRight = new Position[]{
                new Position(x1, y1),
                new Position(x2, y2)
        };
    }

    /**
     * Returns the bounding positions array (top-left at index 0, bottom-right at index 1).
     *
     * @return array of two {@link Position} objects
     */
    public Position[] getTopLeftBottomRight() {
        return topLeftBottomRight;
    }

    /**
     * Returns the top-left corner of this region.
     *
     * @return the top-left {@link Position}
     */
    public Position getTopLeft() {
        return topLeftBottomRight[0];
    }

    /**
     * Returns the bottom-right corner of this region.
     *
     * @return the bottom-right {@link Position}
     */
    public Position getBottomRight() {
        return topLeftBottomRight[1];
    }

    /**
     * Returns the width of this region (difference in x-coordinates).
     *
     * @return width in units
     */
    public int getWidth() {
        return topLeftBottomRight[1].getX() - topLeftBottomRight[0].getX();
    }

    /**
     * Returns the height of this region (difference in y-coordinates).
     *
     * @return height in units
     */
    public int getHeight() {
        return topLeftBottomRight[1].getY() - topLeftBottomRight[0].getY();
    }

    /**
     * Checks whether the given position lies within this region (inclusive).
     *
     * @param position the position to test
     * @return {@code true} if the position is inside or on the boundary of the region
     */
    public boolean contains(Position position) {
        return position.getX() >= topLeftBottomRight[0].getX()
                && position.getX() <= topLeftBottomRight[1].getX()
                && position.getY() >= topLeftBottomRight[0].getY()
                && position.getY() <= topLeftBottomRight[1].getY();
    }

    /**
     * Returns a string representation of this region.
     *
     * @return string in the form {@code Region[topLeft -> bottomRight]}
     */
    @Override
    public String toString() {
        return "Region[" + topLeftBottomRight[0] + " -> " + topLeftBottomRight[1] + "]";
    }
}
