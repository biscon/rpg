package dk.bison.rpg.ui.weapon;

import dk.bison.rpg.core.weapon.WeaponTemplate;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 21-08-2016.
 */
public class WeaponChangeEvent implements MvpEvent {
    public WeaponTemplate weapon;
    public int slot;

    public WeaponChangeEvent(WeaponTemplate weapon, int slot) {
        this.weapon = weapon;
        this.slot = slot;
    }
}
