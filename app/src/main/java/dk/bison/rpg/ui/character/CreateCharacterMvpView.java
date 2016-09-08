package dk.bison.rpg.ui.character;

import dk.bison.rpg.core.armor.Armor;
import dk.bison.rpg.core.character.CharacterStats;
import dk.bison.rpg.core.weapon.Weapon;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface CreateCharacterMvpView extends MvpView {
    void showStats(CharacterStats stats);
    void showClassLevelXp(String class_name, int level, int xp, int xp_next_level);
    void showHpAcAttackBonus(int hp, int ac, int atk_bonus);
    void showArmor(Armor armor);
    void showShield(Armor armor);
    void showMainhand(Weapon weapon);
    void showOffhand(Weapon weapon);
}
