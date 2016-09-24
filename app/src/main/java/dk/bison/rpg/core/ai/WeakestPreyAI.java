package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;

/**
 * Created by bison on 18-08-2016.
 */
public class WeakestPreyAI extends BaseAI {
    public static final String TAG = WeakestPreyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(Encounter encounter) {
        performAttacks(encounter, combatant);
    }

    protected void performAttacks(Encounter encounter, Combatant c)
    {
        List<Attack> attacks = c.getAttacks();
        if(opponent == null)
            opponent = selectWeakestTarget(encounter);
        if(opponent.isDead())
            opponent = selectWeakestTarget(encounter);

        if(opponent == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            return;
        }
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            attack(c, opponent, attack);
            if(opponent.isDead() && i < attacks.size()-1)
            {
                Log.e(TAG, "opponent died while attacking, selecting new target for remaining attacks");
                opponent = selectWeakestTarget(encounter);
                if(opponent == null)
                {
                    Log.e(TAG, "No valid enemies found, doing nothing this round");
                    return;
                }
            }
        }
    }

}
