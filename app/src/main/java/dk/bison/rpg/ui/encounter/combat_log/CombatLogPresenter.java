package dk.bison.rpg.ui.encounter.combat_log;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.mvp.BasePresenter;

/**
 * Created by bison on 19-08-2016.
 */
public class CombatLogPresenter extends BasePresenter<CombatLogMvpView> {
    public static final String TAG = CombatLogPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(CombatLogMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
    }
}
