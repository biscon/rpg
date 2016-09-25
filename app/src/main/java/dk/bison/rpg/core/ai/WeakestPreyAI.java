package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

/**
 * Created by bison on 18-08-2016.
 */
public class WeakestPreyAI extends BaseAI {
    public static final String TAG = WeakestPreyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(Encounter encounter) {
        if(opponent == null)
            opponent = selectWeakestTarget(encounter);
        if(opponent.isDead())
            opponent = selectWeakestTarget(encounter);

        if(opponent == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            return;
        }
        Log.e(TAG, "combatant.pos = " + combatant.getPosition() + ", opponent.pos = " + opponent.getPosition());
        int dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        Log.e(TAG, "Distance to selected opponent is " + dist + "m");
        if(dist > CombatPosition.MELEE_DISTANCE)
        {
            Log.e(TAG, "Target is out of melee range, moving in for the kill");
            performMoveTowardsOpponent(opponent);
        }
        /*
        else
            performAttacks(encounter, combatant);
            */

        dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        if(dist <= CombatPosition.MELEE_DISTANCE)
        {
            performAttacks(encounter, combatant);
        }

    }

    protected void performAttacks(Encounter encounter, Combatant c)
    {
        List<Attack> attacks = c.getAttacks();
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            attack(c, opponent, attack);
            if(opponent.isDead() && i < attacks.size()-1)
            {
                Log.e(TAG, "opponent died while attacking, selecting closest target within melee distance for remaining attacks");
                opponent = selectClosestTargetWithin(encounter, CombatPosition.MELEE_DISTANCE);
                if(opponent == null)
                {
                    Log.e(TAG, "No valid enemies found within melee distance, doing nothing this round");
                    return;
                }
            }
        }
    }

    @Override
    public void reset() {
        opponent = null;
    }
}
