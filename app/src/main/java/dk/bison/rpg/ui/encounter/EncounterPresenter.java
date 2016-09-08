package dk.bison.rpg.ui.encounter;

import android.content.Context;

import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.ui.menu.MenuMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class EncounterPresenter extends BasePresenter<EncounterMvpView> {
    public static final String TAG = EncounterPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(EncounterMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


}
