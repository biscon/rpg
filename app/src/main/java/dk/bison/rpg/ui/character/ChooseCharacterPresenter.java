package dk.bison.rpg.ui.character;

import android.content.Context;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import dk.bison.rpg.core.armor.ACComparator;
import dk.bison.rpg.core.armor.ArmorFactory;
import dk.bison.rpg.core.armor.ArmorTemplate;
import dk.bison.rpg.core.character.Character;
import dk.bison.rpg.core.character.CharacterManager;
import dk.bison.rpg.mvp.BasePresenter;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.mvp.PresentationManager;
import dk.bison.rpg.ui.armor.ArmorChangeEvent;
import dk.bison.rpg.ui.armor.ChooseArmorMvpView;

/**
 * Created by bison on 19-08-2016.
 */
public class ChooseCharacterPresenter extends BasePresenter<ChooseCharacterMvpView> {
    public static final String TAG = ChooseCharacterPresenter.class.getSimpleName();
    List<Character> characters;

    @Override
    public void onCreate(Context context) {
        characters = CharacterManager.instance().getCharacters();
    }

    @Override
    public void attachView(ChooseCharacterMvpView mvpView) {
        super.attachView(mvpView);
        getMvpView().showCharacters(characters);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    public void onCharacterSelected(Character c)
    {
        Log.d(TAG, "Character selected: " + c.getName());
        PresentationManager.instance().publishEvent(new CharacterSelectEvent(c));
        getMvpView().closeView();
    }

    public void deleteCharacter(Context context, Character c)
    {
        CharacterManager.instance().remove(c);
        CharacterManager.instance().save(context);
        getMvpView().showCharacters(characters);
    }

    @Override
    public void onEvent(MvpEvent event) {
        if (event instanceof CharacterCreateEvent) {
            Log.e(TAG, "character created event");
            if(isViewAttached())
                getMvpView().showCharacters(characters);
        }
        if (event instanceof CharacterEditEvent) {
            Log.e(TAG, "character edit event");
            if(isViewAttached())
                getMvpView().showCharacters(characters);
        }
    }
}
