package dk.bison.rpg.ui.encounter.player_control;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface PlayerControlMvpView extends MvpView {
    void show();
    void hide();
    void showCombatant(Combatant c);
    void setCombatants(List<Combatant> combatants);
    void showActionView();
    void showMoveView();
    void showAttackView();
    void setMoveEnabled(boolean enabled);
    void setAttackEnabled(boolean enabled);
    void setTarget(Combatant target);
}
