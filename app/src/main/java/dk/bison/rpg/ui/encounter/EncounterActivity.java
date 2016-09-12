package dk.bison.rpg.ui.encounter;

import android.os.Bundle;

import butterknife.ButterKnife;
import dk.bison.rpg.BaseActivity;
import dk.bison.rpg.R;
import dk.bison.rpg.mvp.PresentationManager;

public class EncounterActivity extends BaseActivity implements EncounterMvpView {
    public static final String TAG = EncounterActivity.class.getSimpleName();
    EncounterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encounter);
        ButterKnife.bind(this);

        // obtain instance to the presenter
        presenter = PresentationManager.instance().presenter(this, EncounterPresenter.class);
        presenter.setup();
        /*
        Monster m1 = MonsterFactory.makeMonster("Wolf");
        Monster m2 = MonsterFactory.makeMonster("Dire Wolf");
        Monster m3 = MonsterFactory.makeMonster("Wolf");

        Character ch1 = new Character();
        ch1.setName("Vanderbilt");
        ch1.giveLevel();
        ch1.giveLevel();
        ch1.equipMainHand(WeaponFactory.makeWeapon("Two-handed Sword"));
        ch1.setArmor(ArmorFactory.makeArmor("Chain Mail"));
        ch1.equipShield(ArmorFactory.makeArmor("Shield"));
        Log.d(TAG, ch1.toString());

        Character ch2 = new Character();
        ch2.setName("Goaloriented Carsten");
        ch2.giveLevel();
        ch2.giveLevel();
        ch2.giveLevel();
        ch2.setArmor(ArmorFactory.makeArmor("Leather Armor"));
        ch2.equipMainHand(WeaponFactory.makeWeapon("Longsword"));
        ch2.equipOffHand(WeaponFactory.makeWeapon("Dagger"));
        Log.d(TAG, ch2.toString());

        Character ch3 = new Character();
        ch3.setName("Bue Lars");
        ch3.giveLevel();
        ch3.giveLevel();
        ch3.equipMainHand(WeaponFactory.makeWeapon("Longbow"));
        ch3.setArmor(ArmorFactory.makeArmor("Leather Armor"));
        Log.d(TAG, ch3.toString());


        BasicEncounter enc = new BasicEncounter();
        enc.addCombatant(ch1);
        enc.addCombatant(ch2);
        enc.addCombatant(ch3);
        enc.addCombatant(m1);
        enc.addCombatant(m2);
        enc.addCombatant(m3);

        enc.fightToDead();
        */

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        presenter.detachView();
        super.onPause();
    }
}
