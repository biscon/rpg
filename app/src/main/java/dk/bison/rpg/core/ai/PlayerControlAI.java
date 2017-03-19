package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.sim.CombatSimulationInterface;
import dk.bison.rpg.core.combat.Combatant;

/**
 * Created by bison on 18-08-2016.
 */
public class PlayerControlAI extends BaseAI {
    public static final String TAG = PlayerControlAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(CombatSimulationInterface combatSimulationInterface) {

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
