package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.sim.CombatSimulationInterface;

/**
 * Created by bison on 18-08-2016.
 */
public class WeakestPreyAI extends BaseAI {
    public static final String TAG = WeakestPreyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(CombatSimulationInterface sim) {
        if(opponent == null)
            opponent = selectWeakestTarget(sim);
        if(opponent.isDead())
            opponent = selectWeakestTarget(sim);

        if(opponent == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            sim.turnDone();
            return;
        }
        Log.e(TAG, "combatant.pos = " + combatant.getPosition() + ", opponent.pos = " + opponent.getPosition());
        int dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        Log.e(TAG, "Distance to selected opponent is " + dist + "m");
        if(dist > CombatPosition.MELEE_DISTANCE)
        {
            Log.e(TAG, "Target is out of melee range, moving in for the kill");
            performMoveTowardsOpponent(sim, opponent);
        }
        /*
        else
            performAttacks(combatSimulationInterface, combatant);
            */

        dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        if(dist <= CombatPosition.MELEE_DISTANCE)
        {
            performAttacks(sim, combatant);
        }
        sim.turnDone();
    }

    protected void performAttacks(CombatSimulationInterface sim, Combatant c)
    {
        List<Attack> attacks = c.getAttacks();
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            attack(sim, c, opponent, attack);
            if(opponent.isDead() && i < attacks.size()-1)
            {
                Log.e(TAG, "opponent died while attacking, selecting closest target within melee distance for remaining attacks");
                opponent = selectClosestTargetWithin(sim, CombatPosition.MELEE_DISTANCE);
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
