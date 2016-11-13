package dk.bison.rpg.ui.encounter;

import java.util.List;
import java.util.Map;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.core.faction.Faction;
import dk.bison.rpg.mvp.MvpEvent;
import dk.bison.rpg.ui.encounter.combat_view.AnimatedCombatant;

/**
 * Created by bison on 13-11-2016.
 */

public class EncounterSetupEvent implements MvpEvent {
    public List<Combatant> combatantList;
    public Map<Combatant, AnimatedCombatant> animatedCombatants;
    public Faction playerFaction;

    public EncounterSetupEvent(List<Combatant> combatantList, Map<Combatant, AnimatedCombatant> animatedCombatants, Faction playerFaction) {
        this.combatantList = combatantList;
        this.playerFaction = playerFaction;
        this.animatedCombatants = animatedCombatants;
    }
}
