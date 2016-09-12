package dk.bison.rpg.ui.encounter;

import android.content.Context;

import dk.bison.rpg.AppState;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.core.monster.Monster;
import dk.bison.rpg.core.monster.MonsterFactory;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.encounter.enemy_status.EnemyStatusPresenter;
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

    public void setup()
    {
        Party party = new Party();
        Monster m1 = MonsterFactory.makeMonster("Wolf", 1);
        Monster m2 = MonsterFactory.makeMonster("Dire Wolf", 1);
        Monster m3 = MonsterFactory.makeMonster("Wolf", 2);
        party.add(m1);
        party.add(m2);
        party.add(m3);
        AppState.enemyParty = party;
    }


}
