package dk.bison.rpg.ui.party;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.core.character.CharacterManager;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.character.CharacterCreateEvent;
import dk.bison.rpg.ui.character.CharacterSelectEvent;
import dk.bison.rpg.ui.character.ChooseCharacterMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class PartyPresenter extends BasePresenter<PartyMvpView> {
    public static final String TAG = PartyPresenter.class.getSimpleName();
    List<Character> characters;

    @Override
    public void onCreate(Context context) {
        characters = new ArrayList<>();
    }

    @Override
    public void attachView(PartyMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().showCharacters(characters);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onPartySelected()
    {
        getMvpView().closeView();
    }

    public void clearCharacters()
    {
        characters.clear();
        if(isViewAttached())
            getMvpView().showCharacters(characters);
    }

    public void removeCharacter(Character c)
    {
        characters.remove(c);
        if(isViewAttached())
            getMvpView().showCharacters(characters);
    }

    @Override
    public void onEvent(MvpEvent event) {
        if (event instanceof CharacterSelectEvent) {
            Character c = ((CharacterSelectEvent) event).character;
            if(characters.contains(c)) // is character already in party?
            {
                if(isViewAttached())
                    getMvpView().showMessage(c.getName() + " is already a member of the party");
                return;
            }
            characters.add(c);
            if(isViewAttached()) {
                getMvpView().showCharacters(characters);
            }
        }
    }
}
