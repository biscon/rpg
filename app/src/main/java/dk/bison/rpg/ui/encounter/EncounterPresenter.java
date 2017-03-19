package dk.bison.rpg.ui.encounter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.Dice;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.ai.PlayerControlAI;
import dk.bison.rpg.core.combat.sim.CombatSimulationInterface;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.InitiativeComparator;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.core.combat.sim.CombatSimulation;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.monster.Monster;
import dk.bison.rpg.core.monster.MonsterFactory;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogIdleEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;
import dk.bison.rpg.ui.encounter.combat_map.MapUpdateEvent;
import dk.bison.rpg.ui.encounter.combat_view.AnimatedCombatant;
import dk.bison.rpg.ui.encounter.combat_view.AnimationStrip;
import dk.bison.rpg.ui.encounter.player_control.PlayerAttackStartedEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerInputRequestEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerInputResponseEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerMoveInfoEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerMoveStartedEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class EncounterPresenter extends BasePresenter<EncounterMvpView> implements CombatSimulationInterface {
    public static final String TAG = EncounterPresenter.class.getSimpleName();
    public static final int IDLE = 0;
    public static final int START_ROUND = 1;
    public static final int ROUND = 2;
    public static final int END_ROUND = 3;
    public static final int END_COMBAT = 4;

    int round;
    List<Combatant> combatants;
    Faction winningFaction;
    Faction playerFaction;
    Iterator<Combatant> curCombatant;
    private int state = IDLE;
    Combatant waitingOnChar = null;

    CombatSimulation simulation;


    @Override
    public void onCreate(Context context) {
        simulation = new CombatSimulation();
    }

    @Override
    public void attachView(EncounterMvpView mvpView) {
        super.attachView(mvpView);
        simulation.start();
    }

    @Override
    public void detachView() {
        simulation.stop();
        super.detachView();
    }


    private void runRound() {
        //Log.e(TAG, "Running state ROUND");
        // are we waiting on player input? do nothing
        if(waitingOnChar != null)
        {
            return;
        }
        // process next combatant if any and check win condition after each
        if(curCombatant.hasNext())
        {
            processCombatant();
            checkWinCondition();
        }
        else // we have run trough all the combatants in this round, check win condition
        {
            if(!checkWinCondition()) {  // if no one won its the end of the round
                state = END_ROUND;
                PresentationManager.instance().publishEvent(new RoundDoneEvent());
                emitMessage(CombatLogMessage.create().roundDone());
            }
        }
        // update map view for good measure
        PresentationManager.instance().publishEvent(new MapUpdateEvent(combatants));
    }

    private void processCombatant()
    {
        Combatant c = curCombatant.next();
        if (!c.isDead()) {
            AI ai = c.getAI();
            if(ai instanceof PlayerControlAI)
            {
                Log.e(TAG, c.getName() + " is player controlled, waiting for input.");
                waitingOnChar = c;
                PresentationManager.instance().publishEvent(new PlayerInputRequestEvent(c, this));
                return;
            }
            else {
                Log.e(TAG, c.getName() + " is AI controlled, performing action.");
                ai.performAction(this);
            }
        }
    }

    private boolean checkWinCondition()
    {
        if(countLivingFactions() == 0)
        {
            state = END_COMBAT;
            waitingOnChar = null;
            emitMessage(CombatLogMessage.create().violent("All factions lost!").effect(CombatLogMessage.ROTATE));
            Log.e(TAG, "All factions lost!");
            getMvpView().postHideNextRoundButton();
            getMvpView().endOfCombat();
            return true;
        }
        else if(countLivingFactions() < 2)
        {
            state = END_COMBAT;
            waitingOnChar = null;
            emitMessage(CombatLogMessage.create().bold("Faction " + winningFaction.getName() + " won the battle!").effect(CombatLogMessage.ROTATE));
            Log.e(TAG, "Faction " + winningFaction.getName() + " won the battle!");
            getMvpView().postHideNextRoundButton();
            getMvpView().endOfCombat();
            return true;
        }
        return false;
    }

    private void runStartRound() {
        Log.e(TAG, "Running state START_ROUND");
        getMvpView().postHideNextRoundButton();
        PresentationManager.instance().publishEvent(new MapUpdateEvent(combatants));
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
            PresentationManager.instance().publishEvent(new MapUpdateEvent(combatants));
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

    public void setup(Context context)
    {
        combatants = new LinkedList<>();
        Party party = new Party();
        Monster m1 = MonsterFactory.makeMonster("Cuddles", "Wolf", 2);
        m1.setPosition(15);
        m1.setLane(0);
        Monster m2 = MonsterFactory.makeMonster(null, "Swearwolf", 2);
        m2.setPosition(12);
        m2.setLane(1);
        Monster m3 = MonsterFactory.makeMonster("Scratchy", "Dire Wolf", 2);
        m3.setPosition(14);
        m3.setLane(2);
        Monster m4 = MonsterFactory.makeMonster("Slikkefanden", "Wolf", 2);
        m4.setGender(Gender.MALE);
        m4.setPosition(16);
        m4.setLane(3);
        party.add(m1);
        party.add(m2);
        party.add(m3);
        party.add(m4);
        AppState.enemyParty = party;
        int pos = -12;
        int lane = 0;
        for(Combatant c : AppState.currentParty) {
            c.setPosition(pos);
            c.setLane(lane);
            lane++;
            pos -= 2;
        }
        addParty(AppState.currentParty);
        addParty(AppState.enemyParty);
        playerFaction = AppState.currentParty.getCombatants().get(0).getFaction();
        Map<Combatant, AnimatedCombatant> animated_combatants = new HashMap<>();
        for(Combatant c : combatants)
        {
            AnimatedCombatant ac = new AnimatedCombatant(c);
            AnimationStrip strip = AnimationStrip.loadStrip(context, "idle_anim_32x64.png", 32, 64, 16, 61);
            ac.walkStrip = strip;
            animated_combatants.put(c, ac);
            if(!c.getFaction().sameAs(playerFaction))
                ac.walkStrip.flip();
        }
        PresentationManager.instance().publishEvent(new EncounterSetupEvent(combatants, animated_combatants, playerFaction));
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

    @Override
    public void combatantDied(Combatant victim, Combatant killer) {

    }

    @Override
    public void turnDone() {

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

    public boolean isWaitingOnInput()
    {
        if(waitingOnChar != null)
            return true;
        else
            return false;
    }

    /**
     * Killer gets half of xp reward, rest is distributed among the player party
     * @param event
     */
    private void awardXPForKill(CombatantDeathEvent event)
    {
        int xp_reward = event.victim.getXPAward();
        int kill_reward = xp_reward / 2;
        int ally_reward = xp_reward / AppState.currentParty.getCombatants().size();
        for(Combatant c : AppState.currentParty.getCombatants())
        {
            if(c.isDead())
                continue;
            if(c == event.killer)
            {
                c.awardXp(kill_reward);
                emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" is rewarded ").bold(String.format(Locale.US, "%d XP", kill_reward)).effect(CombatLogMessage.SLIDE_SCALE_FADE));
            }
            else
            {
                c.awardXp(ally_reward);
                emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" is rewarded ").bold(String.format(Locale.US, "%d XP", ally_reward)));
            }
        }
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof RoundDoneEvent)
        {
            //if(isViewAttached())

        }

        if(event instanceof CombatLogIdleEvent)
        {
            if(isViewAttached())
                getMvpView().postShowNextRoundButton();
        }
        if(event instanceof PlayerInputResponseEvent)
        {
            if(isViewAttached())
                PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
            waitingOnChar = null;
        }
        if(event instanceof PlayerMoveStartedEvent)
        {
            if(isViewAttached())
                getMvpView().gotoTab(EncounterActivity.MAP_TAB);
        }
        if(event instanceof PlayerAttackStartedEvent)
        {
            if(isViewAttached())
                getMvpView().gotoTab(EncounterActivity.STATUS_TAB);
        }
        if(event instanceof CombatantDeathEvent)
        {
            /*
            CombatantDeathEvent cde_event = (CombatantDeathEvent) event;
            if(cde_event.killer.getFaction().sameAs(playerFaction))
            {
                awardXPForKill(cde_event);
            }
            checkWinCondition();
            */
        }
        if(event instanceof CombatLogMessage)
        {
            Log.d(TAG, ((CombatLogMessage) event).getSb().toString());
        }
    }
}
