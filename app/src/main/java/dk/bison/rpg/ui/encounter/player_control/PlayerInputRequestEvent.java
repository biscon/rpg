package dk.bison.rpg.ui.encounter.player_control;

import dk.bison.rpg.core.combat.sim.CombatSimulationInterface;
import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 21-08-2016.
 */
public class PlayerInputRequestEvent implements MvpEvent {
    public Combatant combatant;
    public CombatSimulationInterface combatSimulationInterface;

    public PlayerInputRequestEvent(Combatant c, CombatSimulationInterface e) {
        this.combatant = c;
        this.combatSimulationInterface = e;
    }

}
