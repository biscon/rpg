package dk.bison.rpg.ui.encounter.party_status;

import android.content.Context;
import android.util.Log;

import dk.bison.rpg.AppState;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.ui.encounter.EncounterMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class PartyStatusPresenter extends BasePresenter<PartyStatusMvpView> {
    public static final String TAG = PartyStatusPresenter.class.getSimpleName();

    @Override
    public void onCreate(Context context) {
    }

    @Override
    public void attachView(PartyStatusMvpView mvpView) {
        super.attachView(mvpView);
        Log.e(TAG, "attachView");
        if(AppState.currentParty != null)
        {
            getMvpView().showParty(AppState.currentParty);
        }
    }

    @Override
    public void detachView() {
        Log.e(TAG, "detachView");
        super.detachView();
    }


}
