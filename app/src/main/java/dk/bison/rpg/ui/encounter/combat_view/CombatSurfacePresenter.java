package dk.bison.rpg.ui.encounter.combat_view;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.AppState;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.ui.encounter.StatusUpdateEvent;
import dk.bison.rpg.ui.encounter.enemy_status.EnemySelectNoneEvent;
import dk.bison.rpg.ui.encounter.enemy_status.EnemyStatusMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class CombatSurfacePresenter extends BasePresenter<CombatSurfaceMvpView> {
    public static final String TAG = CombatSurfacePresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(CombatSurfaceMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
    }

    @Override
    public void onEvent(MvpEvent event) {
    }
}
