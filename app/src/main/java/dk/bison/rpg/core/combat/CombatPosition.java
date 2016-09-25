package dk.bison.rpg.core.combat;

/**
 * Created by bison on 24-09-2016.
 * All constants are in meters
 */

public class CombatPosition {
    public static final int CENTER = 0;
    public static final int MOVE_PER_TURN = 8;
    public static final int MELEE_DISTANCE = 2;

    public static int distanceBetweenCombatants(Combatant c1, Combatant c2)
    {
        int dist = Math.abs(c1.getPosition()-c2.getPosition());
        return dist;
    }

}
