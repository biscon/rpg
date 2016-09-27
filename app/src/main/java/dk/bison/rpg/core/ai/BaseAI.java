package dk.bison.rpg.core.ai;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.DistanceComparator;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.core.combat.HPComparator;
import dk.bison.rpg.core.combat.HitInfo;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.MapUpdateEvent;
import dk.bison.rpg.ui.encounter.StatusUpdateEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

/**
 * Created by bison on 18-08-2016.
 */
public abstract class BaseAI extends AI {
    public static final String TAG = BaseAI.class.getSimpleName();
    protected Random random = new Random();
    protected Dice dice = new Dice();
    static float critModifier = 1.5f;

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

    protected Combatant selectClosestTarget(Encounter encounter)
    {
        List<Combatant> combatants = encounter.getCombatants();
        List<Combatant> enemies = new ArrayList<>();
        Faction my_fac = combatant.getFaction();
        for(Combatant c : combatants)
        {
            if(!c.isDead()) {
                if (!my_fac.sameAs(c.getFaction())) {
                    c.setDistanceToCurrentTarget(CombatPosition.distanceBetweenCombatants(combatant, c));
                    enemies.add(c);
                }
            }
        }
        if(enemies.isEmpty())
            return null;
        Log.e(TAG, combatant.getName() + " has " + enemies.size() + " targets, selecting closest");
        Collections.sort(enemies, new DistanceComparator());
        Combatant target = enemies.get(0);
        Log.e(TAG, combatant.getName() + " selected " + target.getName() + " as target. (distance = " + target.getDistanceToCurrentTarget() + ")");
        return target;
    }

    protected Combatant selectClosestTargetWithin(Encounter encounter, int max_dist)
    {
        List<Combatant> combatants = encounter.getCombatants();
        List<Combatant> enemies = new ArrayList<>();
        Faction my_fac = combatant.getFaction();
        for(Combatant c : combatants)
        {
            if(!c.isDead()) {
                if (!my_fac.sameAs(c.getFaction())) {
                    c.setDistanceToCurrentTarget(CombatPosition.distanceBetweenCombatants(combatant, c));
                    enemies.add(c);
                }
            }
        }
        if(enemies.isEmpty())
            return null;
        Log.e(TAG, combatant.getName() + " has " + enemies.size() + " targets, selecting closest");
        Collections.sort(enemies, new DistanceComparator());
        Combatant target = enemies.get(0);
        if(target.getDistanceToCurrentTarget() > max_dist)
        {
            Log.e(TAG, "No enemies found within distance " + max_dist);
            return null;
        }
        Log.e(TAG, combatant.getName() + " selected " + target.getName() + " as target. (distance = " + target.getDistanceToCurrentTarget() + ")");
        return target;
    }

    protected void performMoveTowardsOpponent(Combatant opponent)
    {
        int speed = CombatPosition.MOVE_PER_TURN + (2*combatant.getDEXBonus());
        int diff = Math.abs(opponent.getPosition() - combatant.getPosition());
        Log.e(TAG, String.format(Locale.US, "%s speed = %d", combatant.getName(), speed));
        if(opponent.getPosition() > combatant.getPosition())
        {
            if(diff < speed)
                combatant.setPosition(combatant.getPosition() + diff-1);
            else {
                if(combatant.getPosition()+speed == opponent.getPosition())
                    combatant.setPosition(combatant.getPosition() + speed-1);
                else
                    combatant.setPosition(combatant.getPosition() + speed);
            }
        }
        else
        {
            if(diff < speed)
                combatant.setPosition(combatant.getPosition() - diff+1);
            else {
                if(combatant.getPosition()-speed == opponent.getPosition())
                    combatant.setPosition(combatant.getPosition() - speed+1);
                else
                    combatant.setPosition(combatant.getPosition() - speed);
            }
        }
        int dist = CombatPosition.distanceBetweenCombatants(combatant, opponent);
        int meters = speed;
        if(diff < speed)
            meters = diff;
        emitMessage(CombatLogMessage.create().bold(combatant.getName()).normal(" moved " + meters + " meters towards ").bright(opponent.getName()).dark(String.format(Locale.US, " (%dm)", dist)));
        PresentationManager.instance().publishEvent(new MapUpdateEvent());
    }

    protected void attack(Combatant c, Combatant opponent, Attack attack)
    {
        if(opponent.isDead())
        {
            Log.e(TAG, "opponent is dead, opponent shouldnt be dead here");
        }
        String hittype;
        HitInfo hit = rollToHit(c, opponent, attack.isRanged);
        hittype = "";
        if(hit.type == HitInfo.CRITICAL_HIT || hit.type == HitInfo.CRITICAL_MISS)
            hittype = "(CRITICAL) ";

        if(hit.type == HitInfo.HIT || hit.type == HitInfo.CRITICAL_HIT)
        {
            int damage = rollDamage(c, attack, hit, attack.isRanged);
            CombatLogMessage msg = CombatLogMessage.create().bright(c.getName())
                    .normal(" hits " + opponent.getName()+ " for ")
                    .red(String.valueOf(damage) + " damage.").effect(CombatLogMessage.SLIDE_SCALE_FADE);

            Log.e(TAG, c.getName() + " hits " + opponent.getName() + " for " + damage + " damage. " + hittype + attack.toString());
            opponent.decreaseHP(damage);

            PresentationManager.instance().publishEvent(new StatusUpdateEvent());

            Log.i(TAG, opponent.getName() + " has " + opponent.getHP() + " hitpoints left.");
            msg.normal(" " + opponent.getName() + " has ").bold(String.valueOf(opponent.getHP())).normal(" hitpoints left. ");
            msg.dark(hittype + attack.toString());
            emitMessage(msg);
            if(opponent.isDead()) {
                Log.e(TAG, opponent.getName() + " dies.");
                emitMessage(CombatLogMessage.create().bright(opponent.getName()).violent(" dies!!!").effect(CombatLogMessage.BOUNCE));
            }
        }
        else
        {
            Log.i(TAG, c.getName() + " fails to hit " + opponent.getName() + " " + hittype + attack.toString());
            emitMessage(CombatLogMessage.create().bright(c.getName()).red(" fails").normal(" to hit ").bold(opponent.getName()));
        }
    }

    protected int rollDamage(Combatant chr, Attack attack, HitInfo hit, boolean ranged)
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

    protected HitInfo rollToHit(Combatant chr, Combatant opponent, boolean is_ranged)
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


        int bonus = chr.getSTRBonus();
        if(is_ranged)
            bonus = chr.getDEXBonus();

        int atk_bonus = chr.getAttackBonus();
        Log.e(TAG, "attack = " + attack + " bonus = " + bonus + " atk_bonus = " + atk_bonus);
        int total = attack + bonus + atk_bonus;
        Log.e(TAG, chr.getName() + " to hit is " + total + " opponent AC is " + opponent.getAC());
        if(total >= opponent.getAC())
        {
            return new HitInfo(HitInfo.HIT);
        }
        else {
            return new HitInfo(HitInfo.MISS);
        }
    }

    protected void emitMessage(CombatLogMessage msg)
    {
        PresentationManager.instance().publishEvent(msg);
    }
}
