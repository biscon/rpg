package dk.bison.rpg.ui.encounter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.core.combat.HitInfo;
import dk.bison.rpg.core.combat.InitiativeComparator;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.monster.Monster;
import dk.bison.rpg.core.monster.MonsterFactory;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;
import dk.bison.rpg.ui.encounter.enemy_status.EnemyStatusPresenter;
import dk.bison.rpg.ui.menu.MenuMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class EncounterPresenter extends BasePresenter<EncounterMvpView> implements Encounter {
    public static final String TAG = EncounterPresenter.class.getSimpleName();
    int round;
    List<Combatant> combatants;
    Dice dice = new Dice();
    Faction winningFaction;
    static float critModifier = 1.5f;
    boolean combatDone;

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(EncounterMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void setup()
    {
        combatants = new LinkedList<>();
        round = 1;
        combatDone = false;
        Party party = new Party();
        Monster m1 = MonsterFactory.makeMonster("Wolf", 1);
        Monster m2 = MonsterFactory.makeMonster("Dire Wolf", 1);
        Monster m3 = MonsterFactory.makeMonster("Wolf", 2);
        party.add(m1);
        party.add(m2);
        party.add(m3);
        AppState.enemyParty = party;
        addParty(AppState.currentParty);
        addParty(AppState.enemyParty);
    }

    private void addParty(Party party)
    {
        for(Combatant c : party)
        {
            c.resetHealth();
        }
        combatants.addAll(party.getCombatants());
    }

    public void executeRound()
    {
        if(combatDone) {
            Log.e(TAG, "Combat is done, no more rounds to execute");
            return;
        }
        Log.e(TAG, "Executing round " + round);
        for(Combatant c : combatants)
        {
            int initiative = c.rollInitiative();
            /*
            if(!c.isDead())
                emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" rolls ").bold(String.format(Locale.US, "%d", initiative)).normal(" initiative."));
            */
            //Log.i(TAG, c.getName() + " rolls " + initiative + " initiative.");
        }
        Collections.sort(combatants, new InitiativeComparator());
        if(!combatants.get(0).isDead())
            emitMessage(CombatLogMessage.create().bright(combatants.get(0).getName()).normal(" starts the round."));
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
        if(countLivingFactions() == 0)
        {
            combatDone = true;
            emitMessage(CombatLogMessage.create().violent("All factions lost!"));
            Log.e(TAG, "All factions lost!");
        }
        else if(countLivingFactions() < 2)
        {
            combatDone = true;
            emitMessage(CombatLogMessage.create().bold("Faction ").violent(winningFaction.getName()).bold(" won the battle!"));
            Log.e(TAG, "Faction " + winningFaction.getName() + " won the battle!");
        }
        round++;
    }


    public void emitMessage(CombatLogMessage msg)
    {
        PresentationManager.instance().publishEvent(msg);
    }

    @Override
    public List<Combatant> getCombatants() {
        return combatants;
    }

    @Override
    public void attack(Combatant c, Combatant opponent) {
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
                CombatLogMessage msg = CombatLogMessage.create().bright(c.getName())
                        .normal(" hits " + opponent.getName()+ " for ")
                        .bold(String.valueOf(damage)).red( " damage.");

                Log.e(TAG, c.getName() + " hits " + opponent.getName() + " for " + damage + " damage. " + hittype + attack.toString());
                opponent.decreaseHP(damage);

                PresentationManager.instance().publishEvent(new StatusUpdateEvent());

                Log.i(TAG, opponent.getName() + " has " + opponent.getHP() + " hitpoints left.");
                msg.normal(" " + opponent.getName() + " has ").bold(String.valueOf(opponent.getHP())).normal(" hitpoints left. ");
                msg.dark(hittype + attack.toString());
                emitMessage(msg);
                if(opponent.isDead()) {
                    Log.e(TAG, opponent.getName() + " dies.");
                    emitMessage(CombatLogMessage.create().bright(opponent.getName()).violent(" dies!!!"));
                }
            }
            else
            {
                Log.i(TAG, c.getName() + " fails to hit " + opponent.getName() + " " + hittype + attack.toString());
                emitMessage(CombatLogMessage.create().bright(c.getName()).red(" fails").normal(" to hit ").bold(opponent.getName()));
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

    public boolean isCombatDone() {
        return combatDone;
    }
}
