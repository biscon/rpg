package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.core.combat.HitInfo;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.StatusUpdateEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

/**
 * Created by bison on 18-08-2016.
 */
public class SearchDestroyAI extends BaseAI {
    public static final String TAG = SearchDestroyAI.class.getSimpleName();
    Combatant opponent;

    @Override
    public void performAction(Encounter encounter) {
        performAttacks(encounter, combatant);
    }

    protected void performAttacks(Encounter encounter, Combatant c)
    {
        List<Attack> attacks = c.getAttacks();
        if(opponent == null)
            opponent = selectRandomTarget(encounter);
        if(opponent.isDead())
            opponent = selectRandomTarget(encounter);

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
                opponent = selectRandomTarget(encounter);
                if(opponent == null)
                {
                    Log.e(TAG, "No valid enemies found, doing nothing this round");
                    return;
                }
            }
        }
    }
}
