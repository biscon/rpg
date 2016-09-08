package dk.bison.rpg.core.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.core.combat.HPComparator;
import dk.bison.rpg.core.faction.Faction;

/**
 * Created by bison on 18-08-2016.
 */
public abstract class BaseAI extends AI {
    public static final String TAG = BaseAI.class.getSimpleName();
    protected Random random = new Random();

    protected Combatant selectRandomTarget(Encounter encounter)
    {
        List<Combatant> combatants = encounter.getCombatants();
        List<Combatant> enemies = new ArrayList<>();
        Faction my_fac = combatant.getFaction();
        for(Combatant c : combatants)
        {
            if(!c.isDead()) {
                if (!my_fac.sameAs(c.getFaction()))
                    enemies.add(c);
            }
        }
        if(enemies.isEmpty())
            return null;
        //Log.d(TAG, combatant.getName() + " has " + enemies.size() + " potential targets, selecting random");
        Combatant target = enemies.get(random.nextInt(enemies.size()));
        //Log.d(TAG, combatant.getName() + " selected " + target.getName() + " as target.");
        return target;
    }

    protected Combatant selectWeakestTarget(Encounter encounter)
    {
        List<Combatant> combatants = encounter.getCombatants();
        List<Combatant> enemies = new ArrayList<>();
        Faction my_fac = combatant.getFaction();
        for(Combatant c : combatants)
        {
            if(!c.isDead()) {
                if (!my_fac.sameAs(c.getFaction()))
                    enemies.add(c);
            }
        }
        if(enemies.isEmpty())
            return null;
        //Log.d(TAG, combatant.getName() + " has " + enemies.size() + " potential targets, selecting weakest");
        Collections.sort(enemies, new HPComparator());
        Combatant target = enemies.get(0);
        //Log.d(TAG, combatant.getName() + " selected " + target.getName() + " as target. (HP = " + target.getHP() + ")");
        return target;
    }
}
