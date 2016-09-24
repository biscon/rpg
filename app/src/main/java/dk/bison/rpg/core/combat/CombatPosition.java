package dk.bison.rpg.core.combat;

/**
 * Created by bison on 24-09-2016.
 * All constants are in meters
 */

public class CombatPosition {
    public static final int CENTER = 0;
    public static final int MOVE_PER_TURN = 10;
    public static final int MELEE_DISTANCE = 5;
    public static final int ENGAGED_LEFT_START = 0;
    public static final int ENGAGED_LEFT_END = -5;
    public static final int ENGAGED_RIGHT = 5;
    public static final int NEAR_LEFT = -10;
    public static final int NEAR_RIGHT = 10;
    public static final int FAR_LEFT = -20;
    public static final int FAR_RIGHT = 20;
    public static final int MAX_LEFT = -100;
    public static final int MAX_RIGHT = 100;

    public static int distanceBetweenCombatants(Combatant c1, Combatant c2)
    {
        int dist = Math.abs(c1.getPosition()-c2.getPosition());
        return dist;
    }

}
