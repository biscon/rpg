package dk.bison.rpg.ui.character;

import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 21-08-2016.
 */
public class CharacterSelectEvent implements MvpEvent {
    public Character character;

    public CharacterSelectEvent(Character c) {
        this.character = c;
    }

}
