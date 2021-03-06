package dk.bison.rpg.ui.encounter.player_control;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dk.bison.rpg.core.combat.Attack;
import dk.bison.rpg.core.combat.CombatPosition;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.sim.CombatSimulationInterface;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.combat_map.MapUpdateEvent;
import dk.bison.rpg.ui.encounter.OnBackButtonEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogShowPeriodEvent;
import dk.bison.rpg.ui.encounter.enemy_status.EnemySelectNoneEvent;
import dk.bison.rpg.ui.encounter.enemy_status.EnemySelectedEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class PlayerControlPresenter extends BasePresenter<PlayerControlMvpView> {
    public static final String TAG = PlayerControlPresenter.class.getSimpleName();
    Combatant combatant;
    Combatant currentTarget;
    CombatSimulationInterface combatSimulationInterface;
    public static final int ACTION = 0;
    public static final int MOVE = 1;
    public static final int ATTACK = 2;
    int state = ACTION;
    boolean hasMoved = false;
    List<Attack> usedAttacks = new ArrayList<>();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(PlayerControlMvpView mvpView) {
        super.attachView(mvpView);

    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void endTurn()
    {
        //PresentationManager.instance().publishEvent(CombatLogMessage.create().normal(combatant.getName() + " does nothing like a stupid asshole."));
        PresentationManager.instance().publishEvent(new PlayerInputResponseEvent(combatant));
    }

    private boolean areAnyAttacksPossible()
    {
        boolean possible = false;
        for(Combatant enemy : combatSimulationInterface.getCombatants())
        {
            if(enemy.isDead())
                continue;
            if(enemy.getFaction().sameAs(combatant.getFaction()))
                continue;
            if(!filterAttacks(enemy).isEmpty())
                return true;
        }
        return possible;
    }

    public void gotoAction()
    {
        state = ACTION;
        Log.e(TAG, "setting state to ACTION");
        //PresentationManager.instance().publishEvent(new PlayerMoveStartedEvent(combatant));
        if(isViewAttached()) {
            getMvpView().showActionView();
            if(areAnyAttacksPossible())
                getMvpView().setAttackEnabled(true);
            else
                getMvpView().setAttackEnabled(false);
        }
    }

    public void gotoMove()
    {
        if(hasMoved)
            return;
        state = MOVE;
        Log.e(TAG, "setting state to MOVE");
        PresentationManager.instance().publishEvent(new PlayerMoveStartedEvent(combatant));
        if(isViewAttached())
            getMvpView().showMoveView();
    }

    public void gotoAttack()
    {
        state = ATTACK;
        Log.e(TAG, "setting state to ATTACK");
        PresentationManager.instance().publishEvent(new PlayerAttackStartedEvent(combatant));
        if(isViewAttached()) {
            getMvpView().showAttackView();

        }
    }

    public void performAttack(Attack atk)
    {
        hasMoved = true;
        if(isViewAttached())
            getMvpView().setMoveEnabled(false);
        PresentationManager.instance().publishEvent(new CombatLogShowPeriodEvent(3000));
        usedAttacks.add(atk);
        combatant.getAI().attack(null, combatant, currentTarget, atk);
        if(isViewAttached()) {
            List<Attack> attacks = filterAttacks(currentTarget);
            getMvpView().updateAttacks(attacks);
            if(!areAnyAttacksPossible())
                gotoAction();
        }
    }

    public void performMove(int distance)
    {
        hasMoved = true;
        if(isViewAttached())
            getMvpView().setMoveEnabled(false);
        combatant.getAI().performMove(combatSimulationInterface, distance);
        PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
        PresentationManager.instance().publishEvent(new MapUpdateEvent(combatSimulationInterface.getCombatants()));
        gotoAction();
    }

    private List<Attack> filterAttacks(Combatant target)
    {
        int dist_enemy = CombatPosition.distanceBetweenCombatants(combatant, target);
        List<Attack> attacks_within_range = new ArrayList<>();
        for(Attack atk : combatant.getAttacks())
        {
            if(usedAttacks.contains(atk))
                continue;
            if(dist_enemy <= CombatPosition.MELEE_DISTANCE)
                attacks_within_range.add(atk);
            else if(dist_enemy <= atk.range)
                attacks_within_range.add(atk);
        }
        return attacks_within_range;
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof PlayerInputRequestEvent)
        {
            Log.e(TAG, "Receiving pir event");
            usedAttacks.clear();
            PresentationManager.instance().publishEvent(new EnemySelectNoneEvent());
            PlayerInputRequestEvent pir_event = (PlayerInputRequestEvent) event;
            combatant = pir_event.combatant;
            combatSimulationInterface = pir_event.combatSimulationInterface;
            currentTarget = null;
            state = ACTION;
            hasMoved = false;
            if(isViewAttached()) {
                getMvpView().show();
                getMvpView().setMoveEnabled(true);
                getMvpView().setAttackEnabled(true);
                getMvpView().showCombatant(combatant);
                getMvpView().setTarget(null);
                getMvpView().updateAttacks(null);
            }
            gotoAction();
        }
        if(event instanceof OnBackButtonEvent)
        {
            Log.e(TAG, "OnBackButtonEvent received");
            if(state != ACTION)
            {
                Log.e(TAG, "showing action view");
                //getMvpView().showActionView();
                gotoAction();
            }
        }
        if(event instanceof EnemySelectedEvent && state == ATTACK)
        {
            if(isViewAttached())
            {
                Log.e(TAG, "Receiving es event");
                EnemySelectedEvent es_event = (EnemySelectedEvent) event;
                currentTarget = es_event.combatant;
                getMvpView().setTarget(es_event.combatant);
                // no target
                if(es_event.combatant == null)
                {
                    getMvpView().updateAttacks(null);
                }
                else {
                    List<Attack> attacks = filterAttacks(currentTarget);
                    getMvpView().updateAttacks(attacks);
                }
            }
        }
    }


}
