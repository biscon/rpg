package dk.bison.rpg.ui.encounter.enemy_status;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.mvp.BasePresenter;

/**
 * Created by bison on 19-08-2016.
 */
public class EnemyStatusPresenter extends BasePresenter<EnemyStatusMvpView> {
    public static final String TAG = EnemyStatusPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(EnemyStatusMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
        if(AppState.enemyParty != null)
            getMvpView().showParty(AppState.enemyParty);
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
    }
}
