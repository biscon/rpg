package dk.bison.rpg.ui.encounter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.core.combat.InitiativeComparator;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.monster.Monster;
import dk.bison.rpg.core.monster.MonsterFactory;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogIdleEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

/**
 * Created by bison on 19-08-2016.
 */
public class EncounterPresenter extends BasePresenter<EncounterMvpView> implements Encounter {
    public static final String TAG = EncounterPresenter.class.getSimpleName();
    public static final int IDLE = 0;
    public static final int START_ROUND = 1;
    public static final int ROUND = 2;
    public static final int END_ROUND = 3;
    public static final int END_COMBAT = 4;

    int round;
    List<Combatant> combatants;
    Dice dice = new Dice();
    Faction winningFaction;
    Iterator<Combatant> curCombatant;
    private TimerTask timerTask;
    private Timer timer;
    private int state = IDLE;

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(EncounterMvpView mvpView) {
        super.attachView(mvpView);
        timerTask = new TimerTask() {
            @Override
            public void run() {
                //Log.i(TAG, "timer task running");
                runTick();
            }
        };
        timer = new Timer(true);
        timer.scheduleAtFixedRate(timerTask, 0, 750);
    }

    @Override
    public void detachView() {
        timer.cancel();
        timer.purge();
        super.detachView();
    }

    /**
     * This runs on the timer thread, remember when calling back into the view
     */
    public void runTick()
    {
        switch (state)
        {
            case IDLE:
                runIdle();
                break;
            case START_ROUND:
                runStartRound();
                break;
            case ROUND:
                runRound();
                break;
            case END_ROUND:
                runEndRound();
                break;
            case END_COMBAT:
                runEndCombat();
                break;
        }
    }

    private void runEndCombat() {
        //Log.e(TAG, "Running state END_COMBAT");
    }

    private void runEndRound() {
        //Log.e(TAG, "Running state END_RUN");
    }

    private void runRound() {
        Log.e(TAG, "Running state ROUND");
        if(curCombatant.hasNext())
        {
            Combatant c = curCombatant.next();
            if(!c.isDead())
            {
                AI ai = c.getAI();
                ai.performAction(this);
            }
        }
        else // we have run trough all the combatants in this round
        {
            if(countLivingFactions() == 0)
            {
                state = END_COMBAT;
                emitMessage(CombatLogMessage.create().violent("All factions lost!").effect(CombatLogMessage.ROTATE));
                Log.e(TAG, "All factions lost!");
                getMvpView().postHideNextRoundButton();
            }
            else if(countLivingFactions() < 2)
            {
                state = END_COMBAT;
                emitMessage(CombatLogMessage.create().bold("Faction ").violent(winningFaction.getName()).bold(" won the battle!").effect(CombatLogMessage.ROTATE));
                Log.e(TAG, "Faction " + winningFaction.getName() + " won the battle!");
                getMvpView().postHideNextRoundButton();
            }
            else {
                state = END_ROUND;
                PresentationManager.instance().publishEvent(new RoundDoneEvent());
                emitMessage(CombatLogMessage.create().roundDone());
            }
            getMvpView().postUpdateMapView(combatants);
        }
    }

    private void runStartRound() {
        Log.e(TAG, "Running state START_ROUND");
        getMvpView().postHideNextRoundButton();
        getMvpView().postUpdateMapView(combatants);
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
        curCombatant = combatants.iterator();
        state = ROUND;
        if(isViewAttached())
            getMvpView().postUpdateMapView(combatants);
    }

    private void runIdle() {
    }

    public void startCombat()
    {
        Log.e(TAG, "Starting combat");
        round = 1;
        state = START_ROUND;
    }

    public void startNextRound()
    {
        state = START_ROUND;
        round++;
    }

    public void setup()
    {
        combatants = new LinkedList<>();
        Party party = new Party();
        Monster m1 = MonsterFactory.makeMonster("Cuddles", "Wolf", 2);
        m1.setPosition(10);
        Monster m2 = MonsterFactory.makeMonster(null, "Swearwolf", 2);
        m2.setPosition(15);
        Monster m3 = MonsterFactory.makeMonster("Scratchy", "Dire Wolf", 2);
        m3.setPosition(20);
        Monster m4 = MonsterFactory.makeMonster("Slikkefanden", "Wolf", 2);
        m4.setGender(Gender.MALE);
        m4.setPosition(25);
        party.add(m1);
        party.add(m2);
        party.add(m3);
        party.add(m4);
        AppState.enemyParty = party;
        int pos = -30;
        for(Combatant c : AppState.currentParty) {
            c.setPosition(pos);
            pos -= 2;
        }
        addParty(AppState.currentParty);
        addParty(AppState.enemyParty);
    }

    private void addParty(Party party)
    {
        for(Combatant c : party)
        {
            c.resetHealth();
            c.getAI().reset();
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
        if(state == END_COMBAT)
            return true;
        return false;
    }


    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof RoundDoneEvent)
        {
            //if(isViewAttached())

        }
        if(event instanceof MapUpdateEvent)
        {
            if(isViewAttached())
                getMvpView().postUpdateMapView(combatants);
        }
        if(event instanceof CombatLogIdleEvent)
        {
            getMvpView().postShowNextRoundButton();
        }
    }
}
