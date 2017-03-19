package dk.bison.rpg.core.combat.sim;

/**
 * Created by bison on 18-03-2017.
 */

public abstract class CombatEvent {
    public static final int DEFAULT_DELAY = 2000;

    public long getDelay()
    {
        return DEFAULT_DELAY;
    }
}
