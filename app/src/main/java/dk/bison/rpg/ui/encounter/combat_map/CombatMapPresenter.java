package dk.bison.rpg.ui.encounter.combat_map;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.ui.encounter.player_control.PlayerMoveInfoEvent;

/**
 * Created by bison on 19-08-2016.
 */
public class CombatMapPresenter extends BasePresenter<CombatMapMvpView> {
    public static final String TAG = CombatMapPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(CombatMapMvpView mvpView) {
        super.attachView(mvpView);

    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
    }

    @Override
    public void onEvent(MvpEvent event) {
        if(event instanceof MapUpdateEvent)
        {
            if(isViewAttached())
                getMvpView().drawMap(((MapUpdateEvent) event).combatants);
        }
        if(event instanceof PlayerMoveInfoEvent)
        {
            PlayerMoveInfoEvent pmi_event = (PlayerMoveInfoEvent) event;
            if(isViewAttached()) {
                if(pmi_event.combatant == null)
                    getMvpView().clearMove();
                else
                    getMvpView().displayMove(pmi_event.combatant, pmi_event.value);
            }
        }
    }
    /*
    @Override
    public void showMoveInfoOnMap(Combatant c, int distance) {
        //Log.e(TAG, "Showing gotoMove info on map");
        if(c == null)
            mapView.clearMove();
        else
            mapView.displayMove(c, distance);
    }

    @Override
    public void clearMoveInfoOnMap() {
        mapView.clearMove();
    }
     */
}
