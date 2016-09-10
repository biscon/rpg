package dk.bison.rpg.core.util;

/**
 * Created by bison on 09-08-2015.
 */
public class Coord {
    public int x,y;

    public Coord() {
    }

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        Coord obj = (Coord) o;
        if(obj.x == x && obj.y == y)
            return true;
        else
            return false;
    }

    @Override
    public String toString() {
        return "Coord{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
