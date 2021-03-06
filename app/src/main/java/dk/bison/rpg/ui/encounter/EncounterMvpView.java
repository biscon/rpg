package dk.bison.rpg.ui.encounter;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface EncounterMvpView extends MvpView {
    void postShowNextRoundButton();
    void postHideNextRoundButton();
    void gotoTab(int pos);
    void endOfCombat();
}
