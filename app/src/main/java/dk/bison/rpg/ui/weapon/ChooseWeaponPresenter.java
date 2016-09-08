package dk.bison.rpg.ui.weapon;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dk.bison.rpg.core.armor.ACComparator;
import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.weapon.WeaponFactory;
import dk.bison.rpg.core.weapon.WeaponTemplate;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.armor.ChooseArmorMvpView;
import dk.bison.rpg.ui.character.CreateCharacterPresenter;
import dk.bison.rpg.util.BroadcastReceiver;

/**
 * Created by bison on 19-08-2016.
 */
public class ChooseWeaponPresenter extends BasePresenter<ChooseWeaponMvpView> {
    public static final String TAG = ChooseWeaponPresenter.class.getSimpleName();
    List<WeaponTemplate> weaponTemplates;
    int slot;

    @Override
    public void onCreate(Context context) {
        weaponTemplates = WeaponFactory.getTemplates();
        //Collections.sort(armorTemplates, new ACComparator());
    }

    @Override
    public void attachView(ChooseWeaponMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().showTemplates(weaponTemplates);
    }

    @Override
    public void detachView() {
        super.detachView();
    }


    public void onWeaponSelected(WeaponTemplate template)
    {
        Log.d(TAG, "Weapon selected: " + template.toString());
        PresentationManager.instance().publishEvent(new WeaponChangeEvent(template, slot));
        getMvpView().closeView();
    }

    public void setSlot(int slot)
    {
        this.slot = slot;
    }
}
