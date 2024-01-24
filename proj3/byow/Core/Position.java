package byow.Core;

import java.util.ArrayList;
import java.util.List;

public class Position {
    private int x;
    private int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Position> getNeighbour() {
        List<Position> neighbours = new ArrayList<>();
        if (getX() > 0) {
            neighbours.add(new Position(getX() - 1, getY()));
        }
        if (getY() > 0) {
            neighbours.add(new Position(getX(), getY() - 1));
        }
        if (getX() < Engine.WIDTH - 1) {
            neighbours.add(new Position(getX() + 1, getY()));
        }
        if (getY() < Engine.HEIGHT - 1) {
            neighbours.add(new Position(getX(), getY() + 1));
        }
        return neighbours;
    }

    @Override
    public boolean equals(Object position) {
        if (position instanceof Position) {
            return ((Position) position).getX() == getX() && ((Position) position).getY() == getY();
        }
        return false;
    }
}
