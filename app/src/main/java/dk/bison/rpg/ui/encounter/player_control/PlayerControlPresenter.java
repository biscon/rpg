package dk.bison.rpg.ui.encounter.player_control;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Encounter;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.MapUpdateEvent;
import dk.bison.rpg.ui.encounter.OnBackButtonEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;
import dk.bison.rpg.ui.encounter.enemy_status.EnemySelectedEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class PlayerControlPresenter extends BasePresenter<PlayerControlMvpView> {
    public static final String TAG = PlayerControlPresenter.class.getSimpleName();
    Combatant combatant;
    Encounter encounter;
    public static final int ACTION = 0;
    public static final int MOVE = 1;
    public static final int ATTACK = 2;
    int state = ACTION;
    boolean hasMoved = false;

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

    public void gotoAction()
    {
        state = ACTION;
        Log.e(TAG, "setting state to ACTION");
        PresentationManager.instance().publishEvent(new PlayerMoveStartedEvent(combatant));
        if(isViewAttached())
            getMvpView().showActionView();
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

    public void performMove(int distance)
    {
        hasMoved = true;
        if(isViewAttached())
            getMvpView().setMoveEnabled(false);
        combatant.getAI().performMove(distance);
        PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
        PresentationManager.instance().publishEvent(new MapUpdateEvent());
        gotoAction();
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof PlayerInputRequestEvent)
        {
            PlayerInputRequestEvent pir_event = (PlayerInputRequestEvent) event;
            combatant = pir_event.combatant;
            encounter = pir_event.encounter;
            state = ACTION;
            hasMoved = false;
            if(isViewAttached()) {
                getMvpView().show();
                getMvpView().setMoveEnabled(true);
                getMvpView().setAttackEnabled(true);
                getMvpView().showCombatant(combatant);
            }
        }
        if(event instanceof OnBackButtonEvent)
        {
            Log.e(TAG, "OnBackButtonEvent received");
            if(state != ACTION)
            {
                Log.e(TAG, "showing action view");
                getMvpView().showActionView();
            }
        }
        if(event instanceof EnemySelectedEvent && state == ATTACK)
        {
            if(isViewAttached())
            {
                getMvpView().setTarget(((EnemySelectedEvent) event).combatant);
            }
        }
    }


}
