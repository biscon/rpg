package dk.bison.rpg.core.ai;

import android.util.Log;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;

/**
 * Created by bison on 18-08-2016.
 */
public class WeakestPreyAI extends BaseAI {
    public static final String TAG = WeakestPreyAI.class.getSimpleName();
    private Combatant target;

    @Override
    public void performAction(Encounter encounter) {
        // always select the weakest living target
        target = selectWeakestTarget(encounter);
        if(target == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            return;
        }
        encounter.attack(combatant, target);
    }

}
