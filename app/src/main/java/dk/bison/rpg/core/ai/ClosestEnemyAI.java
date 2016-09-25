package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;

/**
 * Created by bison on 18-08-2016.
 */
public class ClosestEnemyAI extends BaseAI {
    public static final String TAG = ClosestEnemyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(Encounter encounter) {
        if(opponent == null)
            opponent = selectClosestTarget(encounter);
        if(opponent.isDead())
            opponent = selectClosestTarget(encounter);

        if(opponent == null)
        {
            Log.i(TAG, combatant.getName() + " does nothing this round.");
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
                performRangedAttacks(encounter, combatant, dist);
            }
            else {
                Log.e(TAG, "Target is out of melee range, moving in for the kill");
                moved = true;
                performMoveTowardsOpponent(opponent);
            }
        }
        else {
            Log.e(TAG, "Target is within melee range.");
            if(!melee_attacks.isEmpty())
                performMeleeAttacks(encounter, combatant);
            else
                performRangedAttacks(encounter, combatant, dist);
        }

        if(moved) {
            dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
            if (dist <= CombatPosition.MELEE_DISTANCE) {
                Log.e(TAG, "Target is within melee range.");
                if(!melee_attacks.isEmpty())
                    performMeleeAttacks(encounter, combatant);
                else
                    performRangedAttacks(encounter, combatant, dist);
            }
            else
            {
                List<Attack> ranged_attacks = combatant.getRangedAttacks(dist);
                if(!ranged_attacks.isEmpty())
                {
                    Log.e(TAG, "Target is within ranged range.");
                    performRangedAttacks(encounter, combatant, dist);
                }
            }
        }
    }

    protected void performMeleeAttacks(Encounter encounter, Combatant c)
    {
        List<Attack> attacks = c.getMeleeAttacks();
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

    protected void performRangedAttacks(Encounter encounter, Combatant c, int distance)
    {
        List<Attack> attacks = c.getRangedAttacks(distance);
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            attack(c, opponent, attack);
            if(opponent.isDead() && i < attacks.size()-1)
            {
                Log.e(TAG, "opponent died while attacking, selecting closest target within melee distance for remaining attacks");
                opponent = selectClosestTargetWithin(encounter, distance);
                if(opponent == null)
                {
                    Log.e(TAG, "No valid enemies found within melee distance, doing nothing this round");
                    return;
                }
            }
        }
    }

}
