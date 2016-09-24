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
        Monster m1 = MonsterFactory.makeMonster("Cuddles", "Wolf", 1);
        Monster m2 = MonsterFactory.makeMonster(null, "Dire Wolf", 1);
        Monster m3 = MonsterFactory.makeMonster("Scratchy", "Wolf", 2);
        party.add(m1);
        party.add(m2);
        party.add(m3);
        party.add(MonsterFactory.makeMonster(null, "Wolf", 1));
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

    private int findIndexOfFirstLiving()
    {
        for(int i=0; i < combatants.size(); i++)
        {
            if(!combatants.get(i).isDead())
                return i;
        }
        return -1;
    }

    public void executeRound()
    {
        if(combatDone) {
            Log.e(TAG, "Combat is done, no more rounds to execute");
            return;
        }
        if(round > 1)
            emitMessage(CombatLogMessage.create().divider());
        Log.e(TAG, "Executing round " + round);
        for(Combatant c : combatants)
        {
            int initiative = c.rollInitiative();
            //if(!c.isDead())
            //    emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" rolls ").bold(String.format(Locale.US, "%d", initiative)).normal(" initiative."));
            //Log.i(TAG, c.getName() + " rolls " + initiative + " initiative.");
        }
        Collections.sort(combatants, new InitiativeComparator());
        int index_of_first = findIndexOfFirstLiving();
        if(!combatants.get(index_of_first).isDead())
            emitMessage(CombatLogMessage.create().bold(combatants.get(index_of_first).getName()).bright(" starts the round.").effect(CombatLogMessage.ROTATE));
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
            emitMessage(CombatLogMessage.create().violent("All factions lost!").effect(CombatLogMessage.ROTATE));
            Log.e(TAG, "All factions lost!");
        }
        else if(countLivingFactions() < 2)
        {
            combatDone = true;
            emitMessage(CombatLogMessage.create().bold("Faction ").violent(winningFaction.getName()).bold(" won the battle!").effect(CombatLogMessage.ROTATE));
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
