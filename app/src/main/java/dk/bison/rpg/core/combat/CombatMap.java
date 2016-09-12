package dk.bison.rpg.core.combat;

import dk.bison.rpg.core.util.PathFinderMap;

/**
 * Created by bison on 10-09-2016.
 */
public class CombatMap implements PathFinderMap {
    Tile[][] tiles;
    int width;
    int height;

    public CombatMap(int w, int h) {
        tiles = new Tile[w][h];
    }

    @Override
    public boolean isPassable(int x, int y) {
        if(tiles[x][y] != null)
        {
            return tiles[x][y].isPassable;
        }
        return false;
    }

    @Override
    public double cost(int x, int y) {
        if(tiles[x][y] != null)
        {
            return tiles[x][y].cost;
        }
        return Integer.MAX_VALUE;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public class Tile
    {
        public boolean isPassable = true;
        public double cost = 0;
    }
}
