package dk.bison.rpg.core.util;

/**
 * Created by bison on 10-09-2016.
 */
public interface PathFinderMap {
    boolean sample(int x, int y);
    double cost(int x, int y);
}
