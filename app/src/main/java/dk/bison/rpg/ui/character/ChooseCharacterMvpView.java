package dk.bison.rpg.ui.character;

import java.util.List;

import dk.bison.rpg.mvp.MvpView;
import dk.bison.rpg.core.character.Character;

/**
 * Created by bison on 19-08-2016.
 */
public interface ChooseCharacterMvpView extends MvpView {
    void showCharacters(List<Character> characters);
    void closeView();
}
