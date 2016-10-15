package dk.bison.rpg.ui.encounter.combat_map;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.combat.Party;
import dk.bison.rpg.mvp.MvpView;

/**
 * Created by bison on 19-08-2016.
 */
public interface CombatMapMvpView extends MvpView {
    void drawMap(List<Combatant> combatants);
    void displayMove(Combatant c, int distance);
    void clearMove();
}
