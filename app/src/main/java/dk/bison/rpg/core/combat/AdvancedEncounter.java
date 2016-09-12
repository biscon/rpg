package dk.bison.rpg.core.combat;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.faction.Faction;

/**
 * Created by bison on 16-08-2016.
 */
public class AdvancedEncounter implements Encounter {
    public static final String TAG = AdvancedEncounter.class.getSimpleName();
    int round = 1;
    List<Combatant> combatants;
    Dice dice = new Dice();
    Faction winningFaction;
    static float critModifier = 1.5f;
    CombatMap map;

    public AdvancedEncounter() {
        combatants = new LinkedList<>();
    }

    public void addCombatant(Combatant c)
    {
        combatants.add(c);
    }

    public void fightToDead()
    {
        boolean isCombatDone = false;
        while(!isCombatDone && round <= 30)
        {
            executeRound();
            if(countLivingFactions() < 2)
            {
                isCombatDone = true;
                Log.e(TAG, "Faction " + winningFaction.getName() + " won the battle!");
                Log.e(TAG, "-= END OF COMBAT! =-------------------------------------------");
            }
        }
    }

    public void executeRound()
    {
        Log.e(TAG, "-= COMBAT ROUND " + round + " =========================-");
        for(Combatant c : combatants)
        {
            int initiative = c.rollInitiative();
            //Log.i(TAG, c.getName() + " rolls " + initiative + " initiative.");
        }
        Collections.sort(combatants, new InitiativeComparator());
        //Log.i(TAG, combatants.get(0).getName() + " starts the round.");

        Iterator<Combatant> it = combatants.iterator();
        while(it.hasNext())
        {
            Combatant c = it.next();
            if(!c.isDead())
            {
                AI ai = c.getAI();
                ai.performAction(this);
            }
        }
        round++;
    }

    @Override
    public void attack(Combatant c, Combatant opponent)
    {
        String hittype;
        List<Attack> attacks = c.getAttacks();
        for(int i=0; i < attacks.size(); i++)
        {
            Attack attack = attacks.get(i);
            HitInfo hit = rollToHit(c, opponent);
            hittype = "";
            if(hit.type == HitInfo.CRITICAL_HIT || hit.type == HitInfo.CRITICAL_MISS)
                hittype = "(CRITICAL) ";

            if(hit.type == HitInfo.HIT || hit.type == HitInfo.CRITICAL_HIT)
            {
                int damage = rollDamage(c, attack, hit, attack.isRanged);
                Log.e(TAG, c.getName() + " hits " + opponent.getName() + " for " + damage + " damage. " + hittype + attack.toString());
                opponent.decreaseHP(damage);
                Log.i(TAG, opponent.getName() + " have " + opponent.getHP() + " hitpoints left.");
                if(opponent.isDead())
                    Log.e(TAG, opponent.getName() + " dies.");
            }
            else
            {
                Log.i(TAG, c.getName() + " fails to hit " + opponent.getName() + " " + hittype + attack.toString());
            }
            if(opponent.isDead())
                break;
        }
    }

    private int rollDamage(Combatant chr, Attack attack, HitInfo hit, boolean ranged)
    {
        String damage_dice = attack.damage;
        int damage_roll = dice.roll(damage_dice);
        int dmg_bonus = 0;
        if(ranged) // if ranged we use the dex bonus instead
        {
            dmg_bonus = chr.getDEXBonus();
        }
        else {
            float adj_dmg_bonus = chr.getSTRBonus();
            if (attack.type == Attack.TWO_HAND)
                adj_dmg_bonus *= 1.5;
            if (attack.type == Attack.OFF_HAND)
                adj_dmg_bonus *= 0.5;
            dmg_bonus = Math.round(adj_dmg_bonus);
            //Log.e(TAG, "Adjusted STR bonus is " + adj_str_bonus + " rounded to " + str_bonus);
        }
        damage_roll += dmg_bonus;
        if(damage_roll < 1)
            damage_roll = 1;
        if(hit.type == HitInfo.CRITICAL_HIT)
        {
            //Log.d(TAG, "Previous damage roll " + damage_roll);
            damage_roll = Math.round(critModifier * (float) damage_roll);
            //Log.d(TAG, "Applying critmodifier to damage. new damage " + damage_roll);
        }
        return damage_roll;
    }

    private HitInfo rollToHit(Combatant chr, Combatant opponent)
    {
        int attack = dice.roll("1d20");
        if(attack == 1)
        {
            return new HitInfo(HitInfo.CRITICAL_MISS);
        }
        if(attack == 20)
        {
            return new HitInfo(HitInfo.CRITICAL_HIT);
        }

        int str_bonus = chr.getSTRBonus();
        int atk_bonus = chr.getAttackBonus();
        int total = attack + str_bonus + atk_bonus;
        //Log.d(TAG, chr.getName() + " to hit is " + total + " opponent AC is " + opponent.getAC());
        if(total >= opponent.getAC())
        {
            return new HitInfo(HitInfo.HIT);
        }
        else {
            return new HitInfo(HitInfo.MISS);
        }
    }

    @Override
    public List<Combatant> getCombatants() {
        return combatants;
    }

    private int countLivingFactions()
    {
        List<Faction> factions = new ArrayList<>();
        for(Combatant c : combatants)
        {
            if(!c.isDead()) {
                addFactionIfNotPresent(factions, c.getFaction());
            }
        }
        if(factions.size() == 1)
            winningFaction = factions.get(0);
        return factions.size();
    }

    private void addFactionIfNotPresent(List<Faction> factions, Faction faction)
    {
        for(Faction f : factions) {
            if (f.sameAs(faction))
                return;
        }
        factions.add(faction);
    }
}
