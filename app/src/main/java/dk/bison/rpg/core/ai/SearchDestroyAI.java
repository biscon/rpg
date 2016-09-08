package dk.bison.rpg.core.ai;

import android.util.Log;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;

/**
 * Created by bison on 18-08-2016.
 */
public class SearchDestroyAI extends BaseAI {
    public static final String TAG = SearchDestroyAI.class.getSimpleName();
    private Combatant target;

    @Override
    public void performAction(Encounter encounter) {
        // do we have a target? if not select one
        if(target == null)
            target = selectRandomTarget(encounter);

        if(target == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            return;
        }
        // is target dead? select another one
        if(target.isDead())
            target = selectRandomTarget(encounter);
        if(target == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            return;
        }
        encounter.attack(combatant, target);
    }
}
