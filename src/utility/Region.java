package utility;

// Rectangular region on the game map defined by top-left and bottom-right corners.
public class Region {

    // index 0 = top-left, index 1 = bottom-right
    private final Position[] topLeftBottomRight;

    public Region(Position topLeft, Position bottomRight) {
        this.topLeftBottomRight = new Position[]{topLeft, bottomRight};
    }

    public Region(int x1, int y1, int x2, int y2) {
        this.topLeftBottomRight = new Position[]{
                new Position(x1, y1),
                new Position(x2, y2)
        };
    }

    public Position[] getTopLeftBottomRight() {
        return topLeftBottomRight;
    }

    public Position getTopLeft() {
        return topLeftBottomRight[0];
    }

    public Position getBottomRight() {
        return topLeftBottomRight[1];
    }

    public int getWidth() {
        return topLeftBottomRight[1].getX() - topLeftBottomRight[0].getX();
    }

    public int getHeight() {
        return topLeftBottomRight[1].getY() - topLeftBottomRight[0].getY();
    }

    public boolean contains(Position position) {
        return position.getX() >= topLeftBottomRight[0].getX()
                && position.getX() <= topLeftBottomRight[1].getX()
                && position.getY() >= topLeftBottomRight[0].getY()
                && position.getY() <= topLeftBottomRight[1].getY();
    }

    @Override
    public String toString() {
        return "Region[" + topLeftBottomRight[0] + " -> " + topLeftBottomRight[1] + "]";
    }
}
