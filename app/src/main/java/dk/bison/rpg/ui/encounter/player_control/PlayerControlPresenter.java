package dk.bison.rpg.ui.encounter.player_control;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.OnBackButtonEvent;
import dk.bison.rpg.ui.encounter.combat_log.CombatLogMessage;

/**
 * Created by bison on 19-08-2016.
 */
public class PlayerControlPresenter extends BasePresenter<PlayerControlMvpView> {
    public static final String TAG = PlayerControlPresenter.class.getSimpleName();
    Combatant combatant;
    public static final int ACTION = 0;
    public static final int MOVE = 1;
    int state = ACTION;

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

    public void doNothing()
    {
        PresentationManager.instance().publishEvent(CombatLogMessage.create().normal(combatant.getName() + " does nothing like a stupid asshole."));
        PresentationManager.instance().publishEvent(new PlayerInputResponseEvent(combatant));
    }

    public void gotoMove()
    {
        state = MOVE;
        Log.e(TAG, "setting state to MOVE");
        PresentationManager.instance().publishEvent(new PlayerMoveStartedEvent(combatant));
        if(isViewAttached())
            getMvpView().showMoveView();
    }

    public void performMove(int distance)
    {
        combatant.getAI().performMove(distance);
        PresentationManager.instance().publishEvent(new PlayerMoveInfoEvent(null, 0));
        PresentationManager.instance().publishEvent(new PlayerInputResponseEvent(combatant));
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof PlayerInputRequestEvent)
        {
            PlayerInputRequestEvent pir_event = (PlayerInputRequestEvent) event;
            combatant = pir_event.combatant;
            state = ACTION;
            if(isViewAttached()) {
                getMvpView().show();
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
    }


}
