package dk.bison.rpg.ui.encounter;

import dk.bison.rpg.core.combat.Combatant;
import dk.bison.rpg.mvp.MvpEvent;

/**
 * Created by bison on 08-10-2016.
 */

public class CombatantDeathEvent implements MvpEvent {
    public Combatant victim;
    public Combatant killer;

    public CombatantDeathEvent(Combatant victim, Combatant killer) {
        this.victim = victim;
        this.killer = killer;
    }
}
