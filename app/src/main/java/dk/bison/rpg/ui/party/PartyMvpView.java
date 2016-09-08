package dk.bison.rpg.ui.party;

import java.util.List;

import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface PartyMvpView extends MvpView {
    void showCharacters(List<Character> characters);
    void showMessage(String msg);
    void closeView();
}
