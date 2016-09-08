package dk.bison.rpg.ui.armor;

import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 21-08-2016.
 */
public class ArmorChangeEvent implements MvpEvent {
    public ArmorTemplate armor;

    public ArmorChangeEvent(ArmorTemplate armor) {
        this.armor = armor;
    }

}
