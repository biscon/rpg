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
public class ClosestEnemyAI extends BaseAI {
    public static final String TAG = ClosestEnemyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(CombatSimulationInterface sim) {
        if(opponent == null)
            opponent = selectClosestTarget(sim);
        if(opponent.isDead())
            opponent = selectClosestTarget(sim);

        if(opponent == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
            sim.turnDone();
            return;
        }
        List<Attack> melee_attacks = combatant.getMeleeAttacks();
        Log.e(TAG, "combatant.pos = " + combatant.getPosition() + ", opponent.pos = " + opponent.getPosition());
        int dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        Log.e(TAG, "Distance to selected opponent is " + dist + "m");
        boolean moved = false;
        if(dist > CombatPosition.MELEE_DISTANCE)
        {
            List<Attack> ranged_attacks = combatant.getRangedAttacks(dist);
            if(!ranged_attacks.isEmpty())
            {
                Log.e(TAG, "Target is within ranged range.");
                performRangedAttacks(sim, combatant, dist);
            }
            else {
                Log.e(TAG, "Target is out of melee range, moving in for the kill");
                moved = true;
                performMoveTowardsOpponent(sim, opponent);
            }
        }
        else {
            Log.e(TAG, "Target is within melee range.");
            if(!melee_attacks.isEmpty())
                performMeleeAttacks(sim, combatant);
            else
                performRangedAttacks(sim, combatant, dist);
        }

        if(moved) {
            dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
            if (dist <= CombatPosition.MELEE_DISTANCE) {
                Log.e(TAG, "Target is within melee range.");
                if(!melee_attacks.isEmpty())
                    performMeleeAttacks(sim, combatant);
                else
                    performRangedAttacks(sim, combatant, dist);
            }
            else
            {
                List<Attack> ranged_attacks = combatant.getRangedAttacks(dist);
                if(!ranged_attacks.isEmpty())
                {
                    Log.e(TAG, "Target is within ranged range.");
                    performRangedAttacks(sim, combatant, dist);
                }
            }
        }
        sim.turnDone();
    }

    @Override
    public void reset() {
        opponent = null;
    }

    protected void performMeleeAttacks(CombatSimulationInterface sim, Combatant c)
    {
        List<Attack> attacks = c.getMeleeAttacks();
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

    protected void performRangedAttacks(CombatSimulationInterface sim, Combatant c, int distance)
    {
        List<Attack> attacks = c.getRangedAttacks(distance);
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            attack(sim, c, opponent, attack);
            if(opponent.isDead() && i < attacks.size()-1)
            {
                Log.e(TAG, "opponent died while attacking, selecting closest target within melee distance for remaining attacks");
                opponent = selectClosestTargetWithin(sim, distance);
                if(opponent == null)
                {
                    Log.e(TAG, "No valid enemies found within melee distance, doing nothing this round");
                    return;
                }
            }
        }
    }

}
