package dk.bison.rpg.ui.encounter.combat_map;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 26-09-2016.
 */

public class MapUpdateEvent implements MvpEvent {
    public List<Combatant> combatants;

    public MapUpdateEvent(List<Combatant> combatants) {
        this.combatants = combatants;
    }
}
