package dk.bison.rpg.core.combat.sim;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.Gender;
import dk.bison.rpg.core.ai.AI;
import dk.bison.rpg.core.ai.PlayerControlAI;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.InitiativeComparator;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.core.monster.Monster;
import dk.bison.rpg.core.monster.MonsterFactory;

/**
 * Created by bison on 18-03-2017.
 */
/*
    Combat algorithm

    Combat takes place in rounds, start at round 0.

    Each round:
        1. Loop trough each combatant and calculate initiate, sort combatants by initiative to determine who starts the round
        2. Go trough each combatant in order of initiative:
        3. For AI combatant, determine and execute action
        4. For player combatant, pause simulation and wait for player to input valid action
        5. Execute player action (duration varies)
        5. Check victory condition: if only one remaining faction, end simulation, otherwise goto 1.


 */
public class CombatSimulation implements CombatSimulationInterface {
    public static final String TAG = CombatSimulation.class.getSimpleName();
    private List<Combatant> combatants;
    private int round;

    Faction winningFaction;
    Faction playerFaction;
    Iterator<Combatant> curCombatant;

    Queue<CombatEvent> pendingEvents;

    private LooperThread looperThread;
    private Handler handler;
    boolean shouldInsertSetupEvent = true;

    public CombatSimulation() {
        pendingEvents = new ConcurrentLinkedQueue<>();
        combatants = new LinkedList<>();
    }

    public void start()
    {
        looperThread = new LooperThread();
        looperThread.start();
    }

    public void stop()
    {
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessage(-1);
    }

    private void postNextEvent()
    {
        handler.post(new Runnable() {
            @Override
            public void run() {
                executeNextEvent();
            }
        });
    }

    private void postNextEventDelayed(long ms)
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                executeNextEvent();
            }
        }, ms);
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

    private boolean checkWinCondition()
    {
        if(countLivingFactions() == 0)
        {
            Log.e(TAG, "All factions lost!");
            return true;
        }
        else if(countLivingFactions() < 2)
        {
            Log.e(TAG, "Faction " + winningFaction.getName() + " won the battle!");
            return true;
        }
        return false;
    }

    private void addFactionIfNotPresent(List<Faction> factions, Faction faction)
    {
        for(Faction f : factions) {
            if (f.sameAs(faction))
                return;
        }
        factions.add(faction);
    }

    private void onSetupEvent()
    {
        Log.e(TAG, "Executing satup round event");
        combatants.clear();
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

        pendingEvents.offer(new BeginRoundEvent());
        //postNextEventDelayed(CombatEvent.DEFAULT_DELAY);
    }

    private void onBeginRoundEvent() {
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
        Combatant c = combatants.get(index_of_first);
        //if(!combatants.get(index_of_first).isDead())
        //    emitMessage(CombatLogMessage.create().bold(combatants.get(index_of_first).getName()).bright(" starts the round.").effect(CombatLogMessage.ROTATE));
        //Log.i(TAG, combatants.get(0).getName() + " starts the round.");
        curCombatant = combatants.iterator();
        pendingEvents.offer(new ProcessTurnEvent());
    }

    private void onProcessTurnEvent()
    {
        Combatant c = curCombatant.next();
        Log.e(TAG, "Processing combatant " + c.getNameWithTemplateName());
        if (!c.isDead()) {
            AI ai = c.getAI();
            if(ai instanceof PlayerControlAI)
            {
                Log.e(TAG, c.getName() + " is player controlled, skipping.");
            }
            else {
                Log.e(TAG, c.getName() + " is AI controlled, performing action.");
                ai.performAction(this);
                return;
            }
        }
        turnDone();
    }

    private void onEndRoundEvent()
    {
        round++;
        pendingEvents.offer(new BeginRoundEvent());
    }

    private void onEndCombatEvent()
    {
        if(winningFaction != null)
            Log.e(TAG, "Combat ended, winning faction: " + winningFaction.getName());
        else
            Log.e(TAG, "Combat ended, everyone lost");
    }


    private void executeNextEvent()
    {
        long delay = CombatEvent.DEFAULT_DELAY;
        if(!pendingEvents.isEmpty())
        {
            CombatEvent event = pendingEvents.remove();
            delay = event.getDelay();
            Log.e(TAG, "Removing and executing event: " + event.getClass().getSimpleName());
            if(event instanceof SetupEvent) onSetupEvent();
            if(event instanceof BeginRoundEvent) onBeginRoundEvent();
            if(event instanceof ProcessTurnEvent) onProcessTurnEvent();
            if(event instanceof EndRoundEvent) onEndRoundEvent();
            if(event instanceof EndCombatEvent) onEndCombatEvent();
        }
        else
            Log.e(TAG, "Queue is empty");
        postNextEventDelayed(delay);
    }

    @Override
    public List<Combatant> getCombatants() {
        return combatants;
    }

    @Override
    public void combatantDied(Combatant victim, Combatant killer) {
        if(killer.getFaction().sameAs(playerFaction))
        {
            awardXPForKill(victim, killer);
        }
    }

    @Override
    public void turnDone() {
        if(checkWinCondition())
        {
            pendingEvents.offer(new EndCombatEvent());
            return;
        }
        // we've processed all combatants, post end round event
        if(!curCombatant.hasNext())
        {
            pendingEvents.offer(new EndRoundEvent());
            return;
        }
        else
            pendingEvents.offer(new ProcessTurnEvent());
    }

    private void awardXPForKill(Combatant victim, Combatant killer)
    {
        int xp_reward = victim.getXPAward();
        int kill_reward = xp_reward / 2;
        int ally_reward = xp_reward / AppState.currentParty.getCombatants().size();
        for(Combatant c : AppState.currentParty.getCombatants())
        {
            if(c.isDead())
                continue;
            if(c == killer)
            {
                c.awardXp(kill_reward);
                //emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" is rewarded ").bold(String.format(Locale.US, "%d XP", kill_reward)).effect(CombatLogMessage.SLIDE_SCALE_FADE));
            }
            else
            {
                c.awardXp(ally_reward);
                //emitMessage(CombatLogMessage.create().bright(c.getName()).normal(" is rewarded ").bold(String.format(Locale.US, "%d XP", ally_reward)));
            }
        }
    }


    class LooperThread extends Thread
    {
        @Override
        public void run() {
            Log.e(TAG, "Looper thread starting");
            if(shouldInsertSetupEvent) {
                Log.e(TAG, "Inserting setup event");
                pendingEvents.offer(new SetupEvent());
                shouldInsertSetupEvent = false;
            }
            Looper.prepare();
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if(msg.what == -1) {
                        Log.e(TAG, "Receive quit message, quitting looper");
                        Looper.myLooper().quit();
                    }
                    // process incoming messages here
                }
            };
            postNextEventDelayed(CombatEvent.DEFAULT_DELAY);
            Looper.loop();
            Log.e(TAG, "Looper thread exiting from Looper.loop() and quitting");
        }
    }
}
