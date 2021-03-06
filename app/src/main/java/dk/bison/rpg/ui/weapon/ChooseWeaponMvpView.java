package dk.bison.rpg.ui.weapon;

import java.util.List;

import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.weapon.WeaponTemplate;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface ChooseWeaponMvpView extends MvpView {
    void showTemplates(List<WeaponTemplate> templates);
    void closeView();
}
