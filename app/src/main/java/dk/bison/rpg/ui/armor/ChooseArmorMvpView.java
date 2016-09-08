package dk.bison.rpg.ui.armor;

import java.util.List;

import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.character.CharacterStats;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface ChooseArmorMvpView extends MvpView {
    void showTemplates(List<ArmorTemplate> templates);
    void closeView();
}
