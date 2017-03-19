package dk.bison.rpg.core.combat.sim;

import java.util.List;

import dk.bison.rpg.core.combat.Combatant;

/**
 * Created by bison on 18-08-2016.
 */
public interface CombatSimulationInterface {
    List<Combatant> getCombatants();
    void combatantDied(Combatant victim, Combatant killer);
    // signals to the combat simulation it can continue
    void turnDone();
}
